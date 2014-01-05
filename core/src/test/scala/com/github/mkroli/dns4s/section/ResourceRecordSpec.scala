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

import org.scalatest.FunSpec

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.maxInt
import com.github.mkroli.dns4s.maxLong
import com.github.mkroli.dns4s.section.resource.UnknownResource

class ResourceRecordSpec extends FunSpec {
  describe("ResourceRecord") {
    describe("validation") {
      describe("type") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](ResourceRecord("", -1, 0, 0, UnknownResource(Nil, 0)))
          intercept[IllegalArgumentException](ResourceRecord("", maxInt(16) + 1, 0, 0, UnknownResource(Nil, 0)))
        }

        it("should not fail if it is within bounds") {
          ResourceRecord("", 0, 0, 0, UnknownResource(Nil, 0))
          ResourceRecord("", maxInt(16), 0, 0, UnknownResource(Nil, 0))
        }
      }

      describe("class") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](ResourceRecord("", 0, -1, 0, UnknownResource(Nil, 0)))
          intercept[IllegalArgumentException](ResourceRecord("", 0, maxInt(16) + 1, 0, UnknownResource(Nil, 0)))
        }

        it("should not fail if it is within bounds") {
          ResourceRecord("", 0, 0, 0, UnknownResource(Nil, 0))
          ResourceRecord("", 0, maxInt(16), 0, UnknownResource(Nil, 0))
        }
      }

      describe("ttl") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](ResourceRecord("", 0, 0, -1, UnknownResource(Nil, 0)))
          intercept[IllegalArgumentException](ResourceRecord("", 0, 0, maxLong(32) + 1, UnknownResource(Nil, 0)))
        }

        it("should not fail if it is within bounds") {
          ResourceRecord("", 0, 0, 0, UnknownResource(Nil, 0))
          ResourceRecord("", 0, 0, maxLong(32), UnknownResource(Nil, 0))
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(resourceRecord)) should be the same as resourceRecord") {
        def testEncodeDecode(rr: ResourceRecord) {
          assert(rr === ResourceRecord(rr(MessageBuffer()).flipped))
        }
        testEncodeDecode(ResourceRecord("", 0, 0, 0, UnknownResource(Nil, 0)))
        testEncodeDecode(ResourceRecord("test.test.test", maxInt(16), maxInt(16), maxLong(32), UnknownResource(Nil, maxInt(16))))
      }

      it("should prevent infinite loop with compression") {
        val b = MessageBuffer().put(bytes("C000 0000 0000 00000000 0000").toArray).flipped
        intercept[AssertionError](ResourceRecord(b))
      }

      it("should encode/decode a specific byte array") {
        val rr = ResourceRecord("test.test.test", 1, 2, 3, UnknownResource(Nil, 1))(MessageBuffer()).flipped
        assert(bytes("04 74 65 73 74  04 74 65 73 74  04 74 65 73 74 00  0001 0002 00000003 0000") === rr.getBytes(rr.remaining))
      }

      it("should encode/decode a byte array filled with 0s") {
        val rr = ResourceRecord("", 0, 0, 0, UnknownResource(Nil, 0))(MessageBuffer()).flipped
        assert(bytes("00 0000 0000 00000000 0000") === rr.getBytes(rr.remaining))
      }

      it("should encode/decode a byte array filled with mostly 1s") {
        val rr = ResourceRecord("", maxInt(16), maxInt(16), maxLong(32), UnknownResource(Nil, maxInt(16)))(MessageBuffer()).flipped
        assert(bytes("00 FFFF FFFF FFFFFFFF 0000") === rr.getBytes(rr.remaining))
      }
    }
  }
}
