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
package com.github.mkroli.dns4s.netty

import java.net.{InetAddress, InetSocketAddress}

import com.github.mkroli.dns4s.dsl.Query
import io.netty.channel.embedded.EmbeddedChannel
import org.scalatest.funspec.AnyFunSpec

import scala.language.implicitConversions

class DnsCodecSpec extends AnyFunSpec {
  describe("DnsCodec") {
    it("should encode/decode DnsPacket messages") {
      val channel = new EmbeddedChannel(new DnsCodec)
      val message = Query

      val request = DnsPacket(message, new InetSocketAddress(InetAddress.getLocalHost, 53))
      channel.writeOutbound(request)
      channel.writeInbound(channel.readOutbound())
      val response = channel.readInbound().asInstanceOf[DnsPacket]

      assert(response.content ne request.content)
      assert(response.content === request.content)
    }
  }
}
