/*
 * Copyright 2015 Michael Krolikowski, Peter van Rensburg
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

import com.github.mkroli.dns4s.{ MessageBuffer, bytes, maxInt }
import com.github.mkroli.dns4s.section.ResourceRecord
import org.scalatest.FunSpec

class SRVResourceSpec extends FunSpec {
  describe("SRVResource") {
    describe("validation") {
      describe("preference") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](SRVResource(-1, 0, 0, ""))
          intercept[IllegalArgumentException](SRVResource(0, 0, -1, ""))
          intercept[IllegalArgumentException](SRVResource(0, -1, 0, ""))
          intercept[IllegalArgumentException](SRVResource(maxInt(16), maxInt(16), maxInt(16) + 1, ""))
          intercept[IllegalArgumentException](SRVResource(maxInt(16), maxInt(16) + 1, maxInt(16), ""))
          intercept[IllegalArgumentException](SRVResource(maxInt(16) + 1, maxInt(16), maxInt(16), ""))
        }

        it("should not fail if it is within bounds") {
          SRVResource(0, 0, 0, "")
          SRVResource(maxInt(16), maxInt(16), maxInt(16), "")
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        def testEncodeDecode(mr: SRVResource) {
          assert(mr === SRVResource(mr(MessageBuffer()).flipped))
        }
        testEncodeDecode(SRVResource(0, 0, 0, ""))
        testEncodeDecode(SRVResource(maxInt(16), maxInt(16), maxInt(16), "test.test.test"))
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeSRV, 0, 0, SRVResource(123, 234, 345, "test.test"))
        val a = rr(MessageBuffer()).flipped
        val b = bytes("04 74 65 73 74 00  0021 0000 00000000 000D 007B 00EA 0159 04 74 65 73 74 C000")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }
    }
  }
}
