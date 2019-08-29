/*
 * Copyright 2015-2019 Michael Krolikowski
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

import com.github.mkroli.dns4s.dsl.Query

import akka.actor.Actor
import akka.actor.Props

class DnsExtensionActor extends Actor {
  lazy val simple = context.actorOf(Props[DnsSimpleClientActor], "simple")

  override def receive = {
    case Dns.Bind(handler, port, timeout) =>
      implicit val _timeout = timeout
      val requester = sender
      context.actorOf(Props(new DnsActor(port, requester, handler)), s"dns-$port")
    case p @ Dns.DnsPacket(Query(_), _) =>
      simple forward p
  }
}
