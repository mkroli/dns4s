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
import com.github.mkroli.dns4s.{MessageBuffer, bytes, cssGen}
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class TXTResourceSpec extends AnyFunSpec with ScalaCheckPropertyChecks {
  describe("TXTResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        forAll(cssGen) { txt =>
          val cr      = TXTResource(txt)
          val encoded = cr(MessageBuffer()).flipped()
          assert(cr === TXTResource(encoded, encoded.remaining()))
        }
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeTXT, 0, 0, TXTResource(Seq("test.test")))
        val a  = rr(MessageBuffer()).flipped()
        val b  = bytes("04 74 65 73 74 00  0010 0000 00000000 000A 09 74 65 73 74 2E 74 65 73 74")
        assert(b === a.getBytes(a.remaining()))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped()))
      }
    }
  }
}
