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
package com.github.mkroli.dns4s

import com.github.mkroli.dns4s.section.{HeaderSection, QuestionSection, ResourceRecord}
import com.github.mkroli.dns4s.section.resource.CNameResource
import org.scalatest.funspec.AnyFunSpec

import scala.language.implicitConversions

class MessageSpec extends AnyFunSpec {
  describe("Message") {
    describe("encoding/decoding") {
      it("decode(encode(message)) should be the same as message") {
        def testEncodeDecode(m: Message): Unit = {
          assert(m === Message(m().flipped()))
        }
        testEncodeDecode(Message(HeaderSection(0, false, 0, false, false, false, false, 0, 0, 0, 0, 0), Nil, Nil, Nil, Nil))
        testEncodeDecode(
          Message(
            HeaderSection(maxInt(16), true, maxInt(4), true, true, true, true, maxInt(4), 1, 1, 1, 1),
            QuestionSection("test", 1, 2) :: Nil,
            ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil,
            ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil,
            ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil
          )
        )
      }

      it("should encode/decode a specific byte array") {
        val message = Message(
          HeaderSection(maxInt(16), true, maxInt(4), true, true, true, true, maxInt(4), 1, 1, 1, 1),
          QuestionSection("test", 1, 2) :: Nil,
          ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil,
          ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil,
          ResourceRecord("test", ResourceRecord.typeCNAME, 2, 3, CNameResource("test.test")) :: Nil
        )
        val a = message().flipped()
        val b = bytes("""
            FFFF FF8F 0001 0001 0001 0001
            04 74 65 73 74 00  0001 0002
            C00C 0005 0002 00000003 0007 04 74 65 73 74 C00C
            C00C 0005 0002 00000003 0002 C022
            C00C 0005 0002 00000003 0002 C022
          """)
        assert(b === a.getBytes(a.remaining()))
      }
    }
  }
}
