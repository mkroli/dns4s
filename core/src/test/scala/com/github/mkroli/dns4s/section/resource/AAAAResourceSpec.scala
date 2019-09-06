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
package com.github.mkroli.dns4s.section.resource

import java.net.{Inet6Address, InetAddress}

import com.github.mkroli.dns4s.{MessageBuffer, bytes, bytesGenerator}
import com.github.mkroli.dns4s.section.ResourceRecord
import org.scalatest.FunSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class AAAAResourceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks {
  describe("AAAAResource") {
    describe("encoding/decoding") {
      def inet6Address(b: String) =
        InetAddress.getByAddress(bytes(b).toArray).asInstanceOf[Inet6Address]

      it("decode(encode(resource)) should be the same as resource") {
        forAll(bytesGenerator(16, 16)) { addr =>
          val ar = AAAAResource(InetAddress.getByAddress(addr).asInstanceOf[Inet6Address])
          assert(ar === AAAAResource(ar(MessageBuffer()).flipped))
        }
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeAAAA,
          `class` = 0,
          ttl = 0,
          rdata = AAAAResource(inet6Address("0fff fff1 fff2 fff3 fff4 fff5 fff6 fff7"))
        )
        val a = rr(MessageBuffer()).flipped
        val b = bytes("0474 6573 7400 001C 0000 0000 0000 0010 0fff fff1 fff2 fff3 fff4 fff5 fff6 fff7")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }

      it("should encode/decode a byte array filled with 0s") {
        val ar = AAAAResource(inet6Address("0000 0000 0000 0000 0000 0000 0000 0000"))(MessageBuffer()).flipped
        assert(bytes("0000 0000 0000 0000 0000 0000 0000 0000") === ar.getBytes(ar.remaining))
      }

      it("should encode/decode a byte array filled with mostly 1s") {
        val ar = AAAAResource(inet6Address("ffff ffff ffff ffff ffff ffff ffff ffff"))(MessageBuffer()).flipped
        assert(bytes("ffff ffff ffff ffff ffff ffff ffff ffff") === ar.getBytes(ar.remaining))
      }
    }
  }
}
