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

package com.github.mkroli.dns4s.examples.simple.fs2

import cats.effect._
import com.comcast.ip4s.IpLiteralSyntax
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.fs2.Dns

object DnsServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Dns
      .server[IO](port = Some(port"5354")) {
        case Query(q) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
          IO.pure(Response(q) ~ Answers(RRName(host) ~ ARecord("1.2.3.4")))
      }
      .as(ExitCode.Success)
  }
}
