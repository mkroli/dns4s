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

import org.scalatest.FunSpec

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.section.ResourceRecord

class PTRResourceSpec extends FunSpec {
  describe("PTRResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        def testEncodeDecode(pr: PTRResource) {
          assert(pr === PTRResource(pr(MessageBuffer()).flipped))
        }
        testEncodeDecode(PTRResource(""))
        testEncodeDecode(PTRResource("test.test.test"))
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typePTR, 0, 0, PTRResource("test.test"))
        val a = rr(MessageBuffer()).flipped
        val b = bytes("04 74 65 73 74 00  000C 0000 00000000 0007 04 74 65 73 74 C000")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }
    }
  }
}
