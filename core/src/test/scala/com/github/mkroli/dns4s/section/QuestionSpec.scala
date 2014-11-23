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
package com.github.mkroli.dns4s.section

import java.nio.BufferUnderflowException

import org.scalatest.FunSpec

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.maxInt

class QuestionSpec extends FunSpec {
  describe("QuestionSection") {
    describe("validation") {
      describe("qtype") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](QuestionSection("", -1, 0))
          intercept[IllegalArgumentException](QuestionSection("", maxInt(16) + 1, 0))
        }

        it("should not fail if it is within bounds") {
          QuestionSection("", 0, 0)
          QuestionSection("", maxInt(16), 0)
        }
      }

      describe("qclass") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](QuestionSection("", 0, -1))
          intercept[IllegalArgumentException](QuestionSection("", 0, maxInt(16) + 1))
        }

        it("should not fail if it is within bounds") {
          QuestionSection("", 0, 0)
          QuestionSection("", 0, maxInt(16))
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(question)) should be the same as question") {
        def testEncodeDecode(q: QuestionSection) {
          assert(q === QuestionSection(q(MessageBuffer()).flipped))
        }
        testEncodeDecode(QuestionSection("", 0, 0))
        testEncodeDecode(QuestionSection("test.test.test", maxInt(16), maxInt(16)))
      }

      it("should prevent infinite loop with compression") {
        val b = MessageBuffer().put(bytes("C000 0000 0000").toArray).flipped
        intercept[IllegalArgumentException](QuestionSection(b))
      }

      it("should prevent buffer underflows using compression") {
        val b = MessageBuffer().put(bytes("C002").toArray).flipped
        intercept[BufferUnderflowException](QuestionSection(b))
      }

      it("should encode/decode a specific byte array") {
        val q = QuestionSection("test.test.test", 1, 2)(MessageBuffer()).flipped
        assert(bytes("04 74 65 73 74  04 74 65 73 74  04 74 65 73 74 00  0001 0002") === q.getBytes(q.remaining))
      }

      it("should encode/decode a byte array filled with 0s") {
        val q = QuestionSection("", 0, 0)(MessageBuffer()).flipped
        assert(bytes("00 0000 0000") === q.getBytes(q.remaining))
      }

      it("should encode/decode a byte array filled with mostly 1s") {
        val q = QuestionSection("", maxInt(16), maxInt(16))(MessageBuffer()).flipped
        assert(bytes("00 FFFF FFFF") === q.getBytes(q.remaining))
      }
    }
  }
}
