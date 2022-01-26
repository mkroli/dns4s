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

import cats.Functor
import cats.effect.Concurrent
import com.comcast.ip4s._
import fs2._
import fs2.io.net._
import com.github.mkroli.dns4s._

trait DnsServerOps {
  def server[F[_]: Functor: Concurrent: Network](
      address: Option[Host] = None,
      port: Option[Port] = None,
      options: List[DatagramSocketOption] = Nil
  )(handler: Message => F[Message]): F[Unit] = {
    serverPipe(address, port, options) { (s: Stream[F, DnsDatagram]) =>
      s.evalMap { d =>
        Functor[F].map(handler(d.message))(msg => DnsDatagram(d.remote, msg))
      }
    }
  }

  def serverPipe[F[_]: Concurrent: Network](
      address: Option[Host] = None,
      port: Option[Port] = None,
      options: List[DatagramSocketOption] = Nil
  )(handler: Pipe[F, DnsDatagram, DnsDatagram]): F[Unit] = {
    Stream
      .resource(
        Network[F].openDatagramSocket(
          address = address,
          port = port,
          options = options
        )
      )
      .flatMap { socket =>
        socket.reads
          .map(d => DnsDatagram(d.remote, Message(MessageBuffer(d.bytes.toByteBuffer))))
          .through(handler)
          .map(_.datagram)
          .through(socket.writes)
      }
      .compile
      .drain
  }
}
