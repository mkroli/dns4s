/*
 * Copyright 2015 Michael Krolikowski
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
package com.github.mkroli.dns4s.dsl

import com.github.mkroli.dns4s.section.HeaderSection
import org.scalatest.funspec.AnyFunSpec

class HeaderSpec extends AnyFunSpec {
  describe("Header") {
    it("should be possible to create a query") {
      Query match {
        case Response(_) => fail()
        case Query(msg) =>
          assert(msg.header.qr === false)
      }
    }

    it("should be possible to create a response") {
      Response match {
        case Query(_) => fail()
        case Response(msg) =>
          assert(msg.header.qr === true)
      }
    }

    it("should be possible to create a response to a query") {
      Response(Query ~ Id(123) ~ Questions(QName("test") ~ TypeA)) match {
        case Response(msg) ~ Id(123) ~ Questions(QName("test") ~ TypeA() :: Nil) =>
          assert(msg.header.qr === true)
      }
    }

    it("should be possible to set the id") {
      Query ~ Id(123) match {
        case Query(msg ~ Id(123)) =>
          assert(msg.header.id === 123)
      }
    }

    it("should be possible to set the opcode") {
      Query ~ StandardQuery match {
        case Query(msg ~ StandardQuery()) =>
          assert(msg.header.opcode === HeaderSection.opcodeStandardQuery)
      }

      Query ~ InverseQuery match {
        case Query(msg ~ InverseQuery()) =>
          assert(msg.header.opcode === HeaderSection.opcodeInverseQuery)
      }

      Query ~ ServerStatusRequest match {
        case Query(msg ~ ServerStatusRequest()) =>
          assert(msg.header.opcode === HeaderSection.opcodeServerStatusRequest)
      }
    }

    it("should be possible to set flags") {
      Response ~ AuthoritativeAnswer match {
        case Response(msg ~ AuthoritativeAnswer()) =>
          assert(msg.header.aa === true)
      }

      Response ~ Truncation match {
        case Response(msg ~ Truncation()) =>
          assert(msg.header.tc === true)
      }

      Response ~ RecursionDesired match {
        case Response(msg ~ RecursionDesired()) =>
          assert(msg.header.rd === true)
      }

      Response ~ RecursionAvailable match {
        case Response(msg ~ RecursionAvailable()) =>
          assert(msg.header.ra === true)
      }
    }

    it("should be possible to set the rcode") {
      Response ~ NoError match {
        case Response(msg ~ NoError()) =>
          assert(msg.header.rcode === HeaderSection.rcodeNoError)
      }

      Response ~ FormatError match {
        case Response(msg ~ FormatError()) =>
          assert(msg.header.rcode === HeaderSection.rcodeFormatError)
      }

      Response ~ ServerFailure match {
        case Response(msg ~ ServerFailure()) =>
          assert(msg.header.rcode === HeaderSection.rcodeServerFailure)
      }

      Response ~ NameError match {
        case Response(msg ~ NameError()) =>
          assert(msg.header.rcode === HeaderSection.rcodeNameError)
      }

      Response ~ NotImplemented match {
        case Response(msg ~ NotImplemented()) =>
          assert(msg.header.rcode === HeaderSection.rcodeNotImplemented)
      }

      Response ~ Refused match {
        case Response(msg ~ Refused()) =>
          assert(msg.header.rcode === HeaderSection.rcodeRefused)
      }
    }
  }
}
