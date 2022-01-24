/*
 * Copyright 2022 Michael Krolikowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkroli.dns4s.fs2

import cats.ApplicativeError
import cats.data.OptionT
import cats.effect._
import cats.effect.implicits.genSpawnOps
import cats.effect.std.Queue
import cats.syntax.all._
import com.comcast.ip4s._
import com.github.mkroli.dns4s.{Message, MessageBuffer}
import fs2.Stream
import fs2.io.net.Network

import scala.util.Try

class DnsClient[F[_]: Concurrent] private[fs2] (queue: Queue[F, (DnsDatagram, Deferred[F, DnsDatagram])]) {
  def query(query: DnsDatagram): F[DnsDatagram] = {
    for {
      d        <- Deferred[F, DnsDatagram]
      _        <- queue.offer(query, d)
      response <- d.get
    } yield response
  }

  def withDefaultRemote(remote: SocketAddress[IpAddress]) = new DefaultDnsClient[F](queue, remote)
}

class DefaultDnsClient[F[_]: Concurrent] private[fs2] (queue: Queue[F, (DnsDatagram, Deferred[F, DnsDatagram])], remote: SocketAddress[IpAddress])
    extends DnsClient(queue) {
  def queryFor[T](message: Message)(m: PartialFunction[Message, F[T]]): F[T] = {
    for {
      response <- query(DnsDatagram(remote, message))
      mapped = m.lift(response.message)
      mapped <- ApplicativeError[F, Throwable].fromOption(mapped, new MatchError(response.message))
      mapped <- mapped
    } yield mapped
  }
}

trait DnsClientOps {
  def client[F[_]: Concurrent: Network]: Resource[F, DnsClient[F]] = {
    def updatedId(message: Message, id: Int) = message.copy(header = message.header.copy(id = id))

    Network[F]
      .openDatagramSocket()
      .evalMap { socket =>
        for {
          queue       <- Queue.unbounded[F, (DnsDatagram, Deferred[F, DnsDatagram])]
          idGenerator <- Ref[F].of(1)
          store       <- Ref[F].of(Map.empty[(SocketAddress[IpAddress], Int), Deferred[F, DnsDatagram]])
          egress = Stream
            .fromQueueUnterminated(queue)
            .evalMap {
              case (req, deferred) =>
                for {
                  id <- idGenerator.getAndUpdate(id => (id + 1) % 0x10000)
                  msg = updatedId(req.message, id)
                } yield (req.copy(message = msg), deferred)
            }
            .evalTap {
              case (datagram, deferred) =>
                val key = (datagram.remote, datagram.message.header.id)
                store.update(_ + (key -> deferred))
            }
            .map(_._1.datagram)
            .through(socket.writes)
          ingress = socket.reads
            .foreach { d =>
              val o = for {
                dns <- OptionT.fromOption(Try(Message(MessageBuffer(d.bytes.toByteBuffer))).toOption)
                key = (d.remote, dns.header.id)
                store    <- OptionT.liftF(store.getAndUpdate(_ - key))
                deferred <- OptionT.fromOption(store.get(key))
                _        <- OptionT.liftF(deferred.complete(DnsDatagram(d.remote, dns)))
              } yield ()
              o.value.as(())
            }
          handler = egress concurrently ingress
          _ <- handler.compile.drain.start
        } yield new DnsClient(queue)
      }
  }

  def client[F[_]: Concurrent: Network](remote: SocketAddress[IpAddress]): Resource[F, DefaultDnsClient[F]] = client[F].map(_.withDefaultRemote(remote))
}
