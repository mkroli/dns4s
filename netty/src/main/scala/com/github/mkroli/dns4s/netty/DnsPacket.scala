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
package com.github.mkroli.dns4s.netty

import java.net.InetSocketAddress

import com.github.mkroli.dns4s.Message

import io.netty.channel.DefaultAddressedEnvelope

class DnsPacket(msg: Message, dst: InetSocketAddress, src: InetSocketAddress) extends DefaultAddressedEnvelope[Message, InetSocketAddress](msg, dst, src)

object DnsPacket {
  def apply(msg: Message, dst: InetSocketAddress, src: InetSocketAddress) =
    new DnsPacket(msg, dst, src)

  def apply(msg: Message, dst: InetSocketAddress) =
    new DnsPacket(msg, dst, null)
}
