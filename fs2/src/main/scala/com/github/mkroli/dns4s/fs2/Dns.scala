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

import com.comcast.ip4s._
import com.github.mkroli.dns4s._
import _root_.fs2.io.net.Datagram
import _root_.fs2.Chunk

case class DnsDatagram(remote: SocketAddress[IpAddress], message: Message) {
  def datagram = Datagram(remote, Chunk.byteBuffer(message().flipped().buf))
}

object Dns extends DnsServerOps with DnsClientOps
