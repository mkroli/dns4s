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

import java.net.Inet4Address
import java.net.InetAddress

import org.scalatest.FunSpec

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.section.ResourceRecord

class AResourceSpec extends FunSpec {
  describe("AResource") {
    describe("encoding/decoding") {
      def inet4Address(b: String) =
        InetAddress.getByAddress(bytes(b).toArray).asInstanceOf[Inet4Address]

      it("decode(encode(resource)) should be the same as resource") {
        def testEncodeDecode(ar: AResource) {
          assert(ar === AResource(ar(MessageBuffer()).flipped))
        }
        testEncodeDecode(AResource(inet4Address("00 00 00 00")))
        testEncodeDecode(AResource(inet4Address("FF FF FF FF")))
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeA, 0, 0, AResource(inet4Address("FF 0F F0 FF")))
        val a = rr(MessageBuffer()).flipped
        val b = bytes("04 74 65 73 74 00  0001 0000 00000000 0004 FF 0F F0 FF")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }

      it("should encode/decode a byte array filled with 0s") {
        val ar = AResource(inet4Address("00 00 00 00"))(MessageBuffer()).flipped
        assert(bytes("00 00 00 00") === ar.getBytes(ar.remaining))
      }

      it("should encode/decode a byte array filled with mostly 1s") {
        val ar = AResource(inet4Address("FF FF FF FF"))(MessageBuffer()).flipped
        assert(bytes("FF FF FF FF") === ar.getBytes(ar.remaining))
      }
    }
  }
}
