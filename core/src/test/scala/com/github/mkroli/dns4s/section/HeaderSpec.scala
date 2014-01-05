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

class HeaderSpec extends FunSpec {
  lazy val defaultHeader = HeaderSection(0, false, 0, false, false, false, false, 0, 0, 0, 0, 0)

  describe("HeaderSection") {
    describe("validation") {
      describe("id") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultHeader.copy(id = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(id = maxInt(16) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultHeader.copy(id = 0)
          defaultHeader.copy(id = maxInt(16))
        }
      }

      describe("opcode") {
        it("should fail it is out of bounds") {
          intercept[IllegalArgumentException](defaultHeader.copy(opcode = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(opcode = maxInt(4) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultHeader.copy(opcode = 0)
          defaultHeader.copy(opcode = maxInt(4))
        }
      }

      describe("rcode") {
        it("should fail it is out of bounds") {
          intercept[IllegalArgumentException](defaultHeader.copy(rcode = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(rcode = maxInt(4) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultHeader.copy(rcode = 0)
          defaultHeader.copy(rcode = maxInt(4))
        }
      }

      describe("several sections counter") {
        it("should fail it is out of bounds") {
          intercept[IllegalArgumentException](defaultHeader.copy(qdcount = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(ancount = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(nscount = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(arcount = -1))
          intercept[IllegalArgumentException](defaultHeader.copy(qdcount = maxInt(16) + 1))
          intercept[IllegalArgumentException](defaultHeader.copy(ancount = maxInt(16) + 1))
          intercept[IllegalArgumentException](defaultHeader.copy(nscount = maxInt(16) + 1))
          intercept[IllegalArgumentException](defaultHeader.copy(arcount = maxInt(16) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultHeader.copy(qdcount = 0)
          defaultHeader.copy(ancount = 0)
          defaultHeader.copy(nscount = 0)
          defaultHeader.copy(arcount = 0)
          defaultHeader.copy(qdcount = maxInt(16))
          defaultHeader.copy(ancount = maxInt(16))
          defaultHeader.copy(nscount = maxInt(16))
          defaultHeader.copy(arcount = maxInt(16))
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(header)) should be the same as header") {
        def testEncodeDecode(h: HeaderSection) {
          assert(h === HeaderSection(h(MessageBuffer()).flipped))
        }
        testEncodeDecode(defaultHeader)
        testEncodeDecode(HeaderSection(maxInt(16), true, maxInt(4), true, true, true, true, maxInt(4), maxInt(16), maxInt(16), maxInt(16), maxInt(16)))
      }

      it("should encode/decode a specific byte array") {
        val header = HeaderSection(1, false, 2, true, false, true, false, 3, 4, 5, 6, 7)(MessageBuffer()).flipped
        assert(bytes("0001 1503 0004 0005 0006 0007") === header.getBytes(header.remaining))
      }

      it("should encode/decode a byte array filled with 0s") {
        val header = HeaderSection(0, false, 0, false, false, false, false, 0, 0, 0, 0, 0)(MessageBuffer()).flipped
        assert(bytes("0000 0000 0000 0000 0000 0000") == header.getBytes(header.remaining))
      }

      it("should encode/decode a byte array filled with mostly 1s") {
        val header = HeaderSection(maxInt(16), true, maxInt(4), true, true, true, true, maxInt(4), maxInt(16), maxInt(16), maxInt(16), maxInt(16))(MessageBuffer()).flipped
        assert(bytes("FFFF FF8F FFFF FFFF FFFF FFFF") == header.getBytes(header.remaining))
      }
    }
  }
}
