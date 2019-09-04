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
import com.github.mkroli.dns4s.{MessageBuffer, bytes}
import org.scalatest.FunSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class CAAResourceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks {
  describe("CAAResource") {
    describe("validation") {
      describe("flag") {
        it("should fail if flag is less than 0") {
          intercept[IllegalArgumentException](
            CAAResource(-2, "someValue", "someValue")
          )
        }
        it("should fail if flag is more than 255") {
          intercept[IllegalArgumentException](
            CAAResource(256, "someValue", "someValue")
          )
        }
        it("should not fail for a valid value") {
          CAAResource(0, "someValue", "someValue")
          CAAResource(1, "someValue", "someValue")
          CAAResource(100, "someValue", "someValue")
          CAAResource(255, "someValue", "someValue")
        }
      }
      describe("tag") {
        it("should fail if it is empty") {
          intercept[IllegalArgumentException](CAAResource(1, "", "someValue"))
        }
        it("should not fail for a valid value") {
          CAAResource(1, "abc123ABC", "someValue")
        }
      }
    }
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        val cr = CAAResource(1, "someTag", "someValue")
        assert(cr === CAAResource(cr(MessageBuffer()).flipped()))
      }

      it("should be decoded wrapped in CAARecord") {
        val expectedRecord = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeCAA,
          `class` = 0,
          ttl = 0,
          rdata = CAAResource(1, "testTag", "testValue")
        )

        val expectedBytes = bytes(
            "04 74 65 73 74 00 01 01 " +
            "00000000000000 12 01 07 " +
            "74 65 73 74 54 61 67 74 " +
            "65 73 74 56 61 6c 75 65"
        )

        val messageBuffer = expectedRecord(MessageBuffer()).flipped

        val actualBytes = messageBuffer.getBytes(messageBuffer.remaining)

        assert(actualBytes === expectedBytes)

        val actualRecord =
          ResourceRecord(MessageBuffer().put(expectedBytes.toArray).flipped)

        assert(actualRecord === expectedRecord)
      }
    }
  }
}
