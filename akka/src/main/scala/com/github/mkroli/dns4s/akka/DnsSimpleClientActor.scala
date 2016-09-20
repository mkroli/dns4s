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

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import akka.actor.Actor
import akka.actor.Props
import akka.actor.Stash
import akka.util.Timeout

class DnsSimpleClientActor()(implicit val timeout: Timeout = Timeout(5 seconds)) extends Actor with Stash {
  val dnsActor = context.actorOf(Props(new DnsActor(0, self)), "dns")

  override def receive = {
    case Dns.Bound =>
      unstashAll()
      context become bound
    case _ =>
      stash()
  }

  def bound: Receive = {
    case message => dnsActor forward message
  }
}
