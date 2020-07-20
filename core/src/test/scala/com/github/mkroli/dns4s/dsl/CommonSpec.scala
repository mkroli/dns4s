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

import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.section.resource.TXTResource
import org.scalatest.funspec.AnyFunSpec

class CommonSpec extends AnyFunSpec {
  describe("Common") {
    it("should be possible to combine MessageModifierS") {
      val txt = Answers(TXTRecord("test"))
      val ext = EDNS()
      val mm  = txt ~ ext
      Response ~ mm match {
        case Response(Answers(TXTRecord(TXTResource(Seq("test"))) :: Nil) ~ EDNS(_)) =>
      }
    }

    it("should be possible to get a String representation of the RR/Q-Class") {
      def test(`class`: Int, name: String) = {
        Response ~ Questions(QName("test1") ~ QClass(`class`)) ~ Answers(RRName("test2") ~ RRClass(`class`)) match {
          case Response(_) ~ Questions(DnsClassName(`name`) :: Nil) ~ Answers(DnsClassName(`name`) :: Nil) =>
        }
      }

      List(ResourceRecord.classIN -> "IN", ResourceRecord.classCS -> "CS", ResourceRecord.classCH -> "CH", ResourceRecord.classHS -> "HS", 255 -> "Asterisk")
        .foreach {
          case (c, s) => test(c, s)
        }
    }

    it("should be possible to get a String representation of the RR/Q-Type") {
      def test(`type`: Int, name: String) = {
        Response ~ Questions(QName("test1") ~ QType(`type`)) ~ Answers(RRName("test2") ~ RRType(`type`)) match {
          case Response(_) ~ Questions(DnsTypeName(`name`) :: Nil) ~ Answers(DnsTypeName(`name`) :: Nil) =>
        }
      }

      List(
        ResourceRecord.typeA     -> "A",
        ResourceRecord.typeNS    -> "NS",
        ResourceRecord.typeMD    -> "MD",
        ResourceRecord.typeMF    -> "MF",
        ResourceRecord.typeCNAME -> "CNAME",
        ResourceRecord.typeSOA   -> "SOA",
        ResourceRecord.typeMB    -> "MB",
        ResourceRecord.typeMG    -> "MG",
        ResourceRecord.typeMR    -> "MR",
        ResourceRecord.typeNULL  -> "NULL",
        ResourceRecord.typeWKS   -> "WKS",
        ResourceRecord.typePTR   -> "PTR",
        ResourceRecord.typeHINFO -> "HINFO",
        ResourceRecord.typeMINFO -> "MINFO",
        ResourceRecord.typeMX    -> "MX",
        ResourceRecord.typeTXT   -> "TXT",
        ResourceRecord.typeAAAA  -> "AAAA",
        ResourceRecord.typeSRV   -> "SRV",
        ResourceRecord.typeNAPTR -> "NAPTR",
        ResourceRecord.typeOPT   -> "OPT",
        ResourceRecord.typeCAA   -> "CAA",
        252                      -> "AXFR",
        253                      -> "MAILB",
        254                      -> "MAILA",
        255                      -> "Asterisk"
      ).foreach {
        case (t, s) => test(t, s)
      }
    }
  }
}
