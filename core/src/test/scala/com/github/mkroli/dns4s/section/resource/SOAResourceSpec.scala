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
import org.scalatest.prop.PropertyChecks

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.bytes
import com.github.mkroli.dns4s.dnGen
import com.github.mkroli.dns4s.maxLong
import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.ulongGen

class SOAResourceSpec extends FunSpec with PropertyChecks {
  lazy val defaultResource = SOAResource("", "", 0, 0, 0, 0, 0)

  describe("SOAResource") {
    describe("validation") {
      describe("serial") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultResource.copy(serial = -1))
          intercept[IllegalArgumentException](defaultResource.copy(serial = maxLong(32) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultResource.copy(serial = 0)
          defaultResource.copy(serial = maxLong(32))
        }
      }

      describe("refresh") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultResource.copy(refresh = -1))
          intercept[IllegalArgumentException](defaultResource.copy(refresh = maxLong(32) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultResource.copy(refresh = 0)
          defaultResource.copy(refresh = maxLong(32))
        }
      }

      describe("retry") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultResource.copy(retry = -1))
          intercept[IllegalArgumentException](defaultResource.copy(retry = maxLong(32) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultResource.copy(retry = 0)
          defaultResource.copy(retry = maxLong(32))
        }
      }

      describe("expire") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultResource.copy(expire = -1))
          intercept[IllegalArgumentException](defaultResource.copy(expire = maxLong(32) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultResource.copy(expire = 0)
          defaultResource.copy(expire = maxLong(32))
        }
      }

      describe("minimum") {
        it("should fail if it is out of bounds") {
          intercept[IllegalArgumentException](defaultResource.copy(minimum = -1))
          intercept[IllegalArgumentException](defaultResource.copy(minimum = maxLong(32) + 1))
        }

        it("should not fail if it is within bounds") {
          defaultResource.copy(minimum = 0)
          defaultResource.copy(minimum = maxLong(32))
        }
      }
    }

    describe("encoding/decoding") {
      it("decode(encode(resource)) should be the same as resource") {
        forAll(dnGen, dnGen) { (mname, rname) =>
          forAll(ulongGen(32), ulongGen(32), ulongGen(32), ulongGen(32), ulongGen(32)) {
            (serial, refresh, retry, expire, minimum) =>
              val sr = SOAResource(mname, rname, serial, refresh, retry, expire, minimum)
              assert(sr === SOAResource(sr(MessageBuffer()).flipped))
          }
        }
      }

      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeSOA, 0, 0, SOAResource("test.test", "test.test", 1, 2, 3, 4, 5))
        val a = rr(MessageBuffer()).flipped
        val b = bytes("04 74 65 73 74 00  0006 0000 00000000 001D 04 74 65 73 74 C000  C010  00000001 00000002 00000003 00000004 00000005")
        assert(b === a.getBytes(a.remaining))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped))
      }
    }
  }
}
