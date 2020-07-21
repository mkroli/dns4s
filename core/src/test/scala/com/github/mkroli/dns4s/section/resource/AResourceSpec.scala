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

import java.net.{Inet4Address, InetAddress}

import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.{MessageBuffer, bytes}
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.language.implicitConversions

class AResourceSpec extends AnyFunSpec with ScalaCheckPropertyChecks {
  describe("AResource") {
    describe("encoding/decoding") {
      def inet4Address(b: String) =
        InetAddress.getByAddress(bytes(b).toArray).asInstanceOf[Inet4Address]

      it("decode(encode(resource)) should be the same as resource") {
        forAll { (a: Byte, b: Byte, c: Byte, d: Byte) =>
          val ar = AResource(InetAddress.getByAddress(Array(a, b, c, d)).asInstanceOf[Inet4Address])
          assert(ar === AResource(ar(MessageBuffer()).flipped()))
          val encoded = ar(MessageBuffer()).flipped()
          assert(Array(a, b, c, d) === encoded.getBytes(encoded.remaining()))
        }
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeA, 0, 0, AResource(inet4Address("FF 0F F0 FF")))
        val a  = rr(MessageBuffer()).flipped()
        val b  = bytes("04 74 65 73 74 00  0001 0000 00000000 0004 FF 0F F0 FF")
        assert(b === a.getBytes(a.remaining()))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped()))
      }
    }
  }
}
