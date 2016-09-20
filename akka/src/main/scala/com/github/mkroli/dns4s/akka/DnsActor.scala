/*
 * Copyright 2015 Michael Krolikowski
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
package com.github.mkroli.dns4s.akka

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import scala.collection.JavaConversions
import scala.language.postfixOps
import scala.util.Try

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.dsl.Query
import com.github.mkroli.dns4s.dsl.Response
import com.google.common.cache.CacheBuilder

import akka.actor.Actor
import akka.actor.ActorRef
import akka.io.IO
import akka.io.Udp
import akka.io.Udp.CommandFailed
import akka.pattern.ask
import akka.util.ByteString
import akka.util.Timeout

class DnsActor(port: Int, handler: ActorRef)(implicit timeout: Timeout) extends Actor {
  import context.system
  import context.dispatcher

  var nextFreeId = 0

  val requests = JavaConversions.mapAsScalaMap(CacheBuilder.newBuilder()
    .expireAfterWrite(timeout.duration.toSeconds, TimeUnit.SECONDS)
    .build[Integer, ActorRef]()
    .asMap())

  override def preStart() {
    IO(Udp) ! Udp.Bind(self, new InetSocketAddress(port))
  }

  private object MessageInByteString {
    def unapply(bs: ByteString): Option[Message] =
      Try(Message(MessageBuffer(bs.asByteBuffer))).toOption
  }

  override def receive = {
    case Udp.Bound(_) =>
      handler ! Dns.Bound
      context become bound(sender)
    case CommandFailed(Udp.Bind(_, _, _)) =>
      handler ! Dns.Unbound
      context stop self
  }

  def bound(socket: ActorRef): Receive = {
    case Dns.DnsPacket(Query(message), destination) =>
      nextFreeId = (nextFreeId + 1) % 0x10000
      requests.put(nextFreeId, sender)
      socket ! Udp.Send(
        ByteString(message.copy(header = message.header.copy(id = nextFreeId))().flipped.buf),
        destination)
    case Udp.Received(MessageInByteString(Query(message)), remote) =>
      handler ? message onSuccess {
        case Response(response) =>
          socket ! Udp.Send(
            ByteString(response.copy(header = response.header.copy(id = message.header.id))().flipped.buf),
            remote)
      }
    case Udp.Received(MessageInByteString(Response(message)), remote) =>
      requests.get(message.header.id).foreach { sender =>
        sender ! message
      }
    case Dns.Unbind =>
      val s = sender
      socket ! Udp.Unbind
      context become {
        case Udp.Unbound =>
          s ! Dns.Unbound
          context stop self
      }
  }
}
