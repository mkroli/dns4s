/*
 * Copyright 2019 Bilal Fazlani
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
import com.github.mkroli.dns4s.section.resource.CAAResource.{
  IODEFResource,
  IssueResource,
  IssueWildResource,
  CustomCAAResource
}
import com.github.mkroli.dns4s.{MessageBuffer, bytes}
import org.scalatest.FunSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class CAAResourceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks {
  describe("CustomCAAResource") {
    describe("validation") {
      describe("tag") {
        it("should fail if it is empty") {
          intercept[IllegalArgumentException] {
            CustomCAAResource("", "someValue".getBytes, Byte.MinValue)
          }
        }
        it("should not fail for a valid value") {
          CustomCAAResource("someTag", "someValue".getBytes, Byte.MinValue)
        }
      }
    }
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        val expectedResource =
          CustomCAAResource("someTag", "someValue".getBytes, 1.toByte)
        val encoded = expectedResource(MessageBuffer()).flipped()
        val actualResource = CAAResource(encoded, encoded.remaining())
        assert(expectedResource === actualResource)
        assert(
          expectedResource.copy(
            valueBytes = expectedResource.valueBytes.map(b => (~b).toByte)
          ) !== actualResource
        )
      }

      it("should be decoded wrapped in CAARecord") {
        val expectedRecord = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeCAA,
          `class` = 0,
          ttl = 0,
          rdata = CustomCAAResource("testTag", "testValue".getBytes, 1.toByte)
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
  describe("IssueResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        val expectedResource =
          IssueResource("someValue", issuerCritical = true)
        val encoded = expectedResource(MessageBuffer()).flipped()
        val actualResource = CAAResource(encoded, encoded.remaining())
        assert(expectedResource === actualResource)
      }

      it("should be decoded wrapped in CAARecord") {
        val expectedRecord = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeCAA,
          `class` = 0,
          ttl = 0,
          rdata = IssueResource("testValue", issuerCritical = true)
        )

        val expectedBytes = bytes(
          "04 74 65 73 74 00 01 01" +
            "00 00 00 00 00 00 00 10" +
            "01 05 69 73 73 75 65 74" +
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
  describe("IssueWildResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        val expectedResource =
          IssueWildResource("someValue", issuerCritical = false)
        val encoded = expectedResource(MessageBuffer()).flipped()
        val actualResource = CAAResource(encoded, encoded.remaining())
        assert(expectedResource === actualResource)
      }

      it("should be decoded wrapped in CAARecord") {
        val expectedRecord = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeCAA,
          `class` = 0,
          ttl = 0,
          rdata = IssueWildResource("testValue", issuerCritical = true)
        )

        val expectedBytes = bytes(
          "04 74 65 73 74 00 01 01" +
            "00 00 00 00 00 00 00 14" +
            "01 09 69 73 73 75 65 77" +
            "69 6c 64 74 65 73 74 56" +
            "61 6c 75 65"
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
  describe("IODEFResource") {
    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        val expectedResource =
          IODEFResource("someValue")
        val encoded = expectedResource(MessageBuffer()).flipped()
        val actualResource = CAAResource(encoded, encoded.remaining())
        assert(expectedResource === actualResource)
      }

      it("should be decoded wrapped in CAARecord") {
        val expectedRecord = ResourceRecord(
          name = "test",
          `type` = ResourceRecord.typeCAA,
          `class` = 0,
          ttl = 0,
          rdata = IODEFResource("testValue")
        )

        val expectedBytes = bytes(
          "04 74 65 73 74 00 01 01" +
            "00 00 00 00 00 00 00" +
            "10 00 05 69 6f 64 65" +
            "66 74 65 73 74 56 61" +
            "6c 75 65"
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
