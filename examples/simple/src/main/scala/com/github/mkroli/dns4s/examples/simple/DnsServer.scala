/*
 * Copyright 2014 Michael Krolikowski
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
package com.github.mkroli.dns4s.examples.simple

import akka.actor._
import akka.io.IO
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.akka._
import scala.concurrent.duration._

class DnsHandlerActor extends Actor {
  override def receive = {
    case Query() ~ Questions(QName(host) ~ TypeA() :: Nil) =>
      sender ! Response ~ Questions(host) ~ Answers(ARecord("1.2.3.4"))
  }
}

object DnsServer extends App {
  implicit val system = ActorSystem("DnsServer")
  IO(Dns) ! Dns.Bind(system.actorOf(Props[DnsHandlerActor]), 5354)
}
