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
package com.github.mkroli.dns4s.section.resource

import org.scalatest.FunSpec
import org.scalatest.prop.PropertyChecks

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.bytesGenerator
import com.github.mkroli.dns4s.section.ResourceRecord

class OPTResourceSpec extends FunSpec with PropertyChecks {
  describe("OPTResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        forAll(bytesGenerator()) { bytes =>
          val r: OPTResource = OPTResource(1, 1, 1, 24, 0, "127.0.0.1")
          val encoded = r.apply(MessageBuffer()).put(bytes).flipped()
          assert(r === OPTResource(encoded, bytes.length))
        }
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeOPT, 0, 0, OPTResource(1, 1, 1, 24, 0, "127.0.0.1"))
        val a = rr(MessageBuffer()).flipped()
        val b = bytes("04 74 65 73 74 00  0029 0000 00000000 0000")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped()))
      }
    }
  }
}
