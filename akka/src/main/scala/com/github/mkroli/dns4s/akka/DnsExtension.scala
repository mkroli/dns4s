/*
 * Copyright 2013-2015 Michael Krolikowski
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

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import com.github.mkroli.dns4s.Message

import akka.actor.ActorRef
import akka.actor.ExtendedActorSystem
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.Props
import akka.io.IO.Extension
import akka.util.Timeout

object Dns extends ExtensionId[DnsExtension] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem) = new DnsExtension(system)

  override def lookup() = Dns

  case class Bind(handler: ActorRef, port: Int, implicit val timeout: Timeout = 5 seconds)

  case object Bound

  case object Unbind

  case object Unbound

  case class DnsPacket(message: Message, destination: InetSocketAddress)
}

class DnsExtension(system: ExtendedActorSystem) extends Extension {
  override val manager = system.actorOf(Props[DnsExtensionActor](), "dns4s")
}
