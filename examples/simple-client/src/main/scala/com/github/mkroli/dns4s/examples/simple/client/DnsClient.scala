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
package com.github.mkroli.dns4s.examples.simple.client

import java.net.InetSocketAddress

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import com.github.mkroli.dns4s.akka.Dns
import com.github.mkroli.dns4s.dsl._

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

object DnsClient extends App {
  implicit val system = ActorSystem("DnsServer")
  implicit val timeout = Timeout(5 seconds)
  import system.dispatcher

  IO(Dns) ? Dns.DnsPacket(Query ~ Questions("google.de"), new InetSocketAddress("8.8.8.8", 53)) onSuccess {
    case Response(Answers(answers)) =>
      answers.collect {
        case ARecord(arecord) => println(arecord.address.getHostAddress)
      }
      system.shutdown
  }
}
