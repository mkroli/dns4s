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

package com.github.mkroli.dns4s.examples.simple.fs2.client

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{IpLiteralSyntax, SocketAddress}
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.fs2.Dns

object DnsClient extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Dns
      .client[IO](SocketAddress(ip"8.8.8.8", port"53"))
      .use { dns =>
        for {
          address <- dns.queryFor(Query ~ Questions(QName("google.com"))) {
            case Response(Answers(answers)) => IO.fromOption {
              answers.collectFirst {
                case ARecord(arecord) => arecord.address.getHostAddress
              }
            }(new RuntimeException("Response doesn't contain A-Record"))
          }
          _ <- IO.println(address)
        } yield ExitCode.Success
      }
  }
}
