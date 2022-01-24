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

import cats.effect.{IO, ParallelF}
import cats.effect.testing.scalatest._
import com.comcast.ip4s.{Host, IpAddress, Port, SocketAddress}
import com.github.mkroli.dns4s.{DnsTestUtils, Message}
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.section.resource.AResource
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class DnsSpec extends AsyncFunSpec with AsyncIOSpec with Matchers with DnsTestUtils {
  val host = Host.fromString("127.0.0.1").map(_.asInstanceOf[IpAddress])
  val port = Port.fromInt(nextAvailablePort())

  val server = Dns.server[IO](address = host, port = port) {
    case Query(msg) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
      IO.pure(
        Response(msg) ~ Answers(RRName(host) ~ ARecord("1.2.3.4"))
      )
  }

  val client = Dns
    .client[IO](SocketAddress(host.get, port.get))

  val clientWithServer = server.background.flatMap(_ => client)

  describe("Dns") {
    it("should be possible to send and receive messages") {
      clientWithServer
        .use { client =>
          client.queryFor(Query ~ Questions(QName("test.test"))) {
            case resp => IO.pure(resp)
          }
        }
        .timeout(3.seconds)
        .asserting {
          case Response(_) ~ Answers(RRName("test.test") ~ ARecord(AResource(ip)) :: Nil) if ip.getHostAddress == "1.2.3.4" => succeed
          case _                                                                                                            => fail()
        }
    }

    it("should raise an error if the response doesn't match expectations") {
      clientWithServer
        .use { client =>
          client.queryFor(Query ~ Questions(QName("test.test")))(PartialFunction.empty)
        }
        .timeout(3.seconds)
        .assertThrows[MatchError]
    }
  }
}
