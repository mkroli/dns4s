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

import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s._
import org.scalatest.FunSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class NAPTRResourceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks {
  describe("NAPTRResource") {
    describe("validation") {
      describe("order") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](NAPTRResource(-1, 0, "", "", "", ""))
          intercept[IllegalArgumentException](NAPTRResource(maxInt(16) + 1, 0, "", "", "", ""))
        }

        it("should not fail if it is within bounds") {
          NAPTRResource(0, 0, "", "", "", "")
          NAPTRResource(maxInt(16), 0, "", "", "", "")
        }
      }

      describe("preference") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](NAPTRResource(0, -1, "", "", "", ""))
          intercept[IllegalArgumentException](NAPTRResource(0, maxInt(16) + 1, "", "", "", ""))
        }

        it("should not fail if it is within bounds") {
          NAPTRResource(0, 0, "", "", "", "")
          NAPTRResource(0, maxInt(16), "", "", "", "")
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        forAll(uintGen(16), uintGen(16), csGen, csGen, csGen, dnGen) { (order, preference, flags, services, regexp, replacement) =>
          val mr = NAPTRResource(order, preference, flags, services, regexp, replacement)
          assert(mr === NAPTRResource(mr(MessageBuffer()).flipped))
        }
      }
      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeNAPTR, 0, 0, NAPTRResource(123, 456, "ABC", "DEF", "GHI", "abc.def.ghi"))
        val a = rr(MessageBuffer()).flipped
        val b = bytes("""04 74 65 73 74 00  0023 0000 00000000 001D
                         00 7B
                         01 C8
                         03 41 42 43
                         03 44 45 46
                         03 47 48 49
                         03 61 62 63 03 64 65 66 03 67 68 69 00""")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }
    }
  }
}
