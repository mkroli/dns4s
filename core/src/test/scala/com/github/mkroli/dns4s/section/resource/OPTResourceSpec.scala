/*
 * Copyright 2015-2017 Michael Krolikowski
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

import java.net.InetAddress

import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.section.resource.OPTResource.ClientSubnetOPTOptionData
import com.github.mkroli.dns4s.{MessageBuffer, bytes, bytesGenerator}
import org.scalatest.FunSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class OPTResourceSpec extends FunSpec with ScalaCheckDrivenPropertyChecks {
  describe("OPTResource") {
    describe("encoding/decoding") {
      it("should be decoded wrapped in ResourceRecord") {
        val rr = ResourceRecord("test", ResourceRecord.typeOPT, 0, 0, OPTResource(Nil))
        val a  = rr(MessageBuffer()).flipped()
        val b  = bytes("04 74 65 73 74 00  0029 0000 00000000 0000")
        assert(b === a.getBytes(a.remaining()))
        assert(rr === ResourceRecord(MessageBuffer().put(b.toArray).flipped()))
      }
    }

    describe("ClientSubnetOPTOptionData") {
      it("decode(encode(resource)) should be the same as resource") {
        val r = OPTResource(
          OPTResource.OPTOption(
            OPTResource.optionClientSubnet,
            OPTResource.ClientSubnetOPTOptionData(ClientSubnetOPTOptionData.familyIPv4, 32, 32, InetAddress.getByAddress(Array(-1, -1, -1, -1)))
          ) :: Nil
        )
        val encoded = r(MessageBuffer()).flipped()

        OPTResource(encoded, encoded.remaining()).options match {
          case OPTResource.OPTOption(
                OPTResource.optionClientSubnet,
                OPTResource.ClientSubnetOPTOptionData(ClientSubnetOPTOptionData.familyIPv4, 32, 32, address)
              ) :: Nil =>
            assert(address.getAddress === Array(-1, -1, -1, -1))
          case actual => fail(s"unexpected result: $actual")
        }
      }

      it("should encode/decode ClientSubnetOPTOptionData using IPv4 family") {
        val reference = ClientSubnetOPTOptionData(ClientSubnetOPTOptionData.familyIPv4, 24, 8, InetAddress.getByAddress(Array(-1, -1, -1, 0)))
        val buffer    = reference(MessageBuffer()).flipped()
        val decoded   = ClientSubnetOPTOptionData(buffer, buffer.remaining())
        assert(decoded === reference)
      }

      it("should encode/decode ClientSubnetOPTOptionData using IPv6 family") {
        val reference = ClientSubnetOPTOptionData(
          ClientSubnetOPTOptionData.familyIPv6,
          16 * 8,
          16 * 8,
          InetAddress.getByAddress(Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1))
        )
        val buffer  = reference(MessageBuffer()).flipped()
        val decoded = ClientSubnetOPTOptionData(buffer, buffer.remaining())
        assert(decoded === reference)
      }

      def testBitmask(family: Int): Unit = {
        val bits = family match {
          case ClientSubnetOPTOptionData.familyIPv4 => 32
          case ClientSubnetOPTOptionData.familyIPv6 => 128
        }

        (0 to bits).foreach { i =>
          val reference = ClientSubnetOPTOptionData(family, i, 8, InetAddress.getByAddress(Array.fill(bits / 8)(-1)))
          val buffer    = reference(MessageBuffer()).flipped()
          val decoded   = ClientSubnetOPTOptionData(buffer, buffer.remaining())
          val address   = decoded.address.getAddress

          val expectedCompressed = {
            val fullBytes     = Array.fill[Byte](i / 8)(-1)
            val remainingBits = i % 8
            val halfByte = if (remainingBits > 0) {
              val ones    = (1 << remainingBits) - 1
              val shifted = ones << (8 - remainingBits)
              Array(shifted.toByte)
            } else {
              Array.emptyByteArray
            }
            fullBytes ++ halfByte
          }
          val expected = expectedCompressed ++ Array.fill[Byte](bits / 8 - expectedCompressed.length)(0)

          assert(address === expected)
        }
      }

      it("should apply the bitmask to the IPv4 address according to sourcePrefixLength") {
        testBitmask(ClientSubnetOPTOptionData.familyIPv4)
      }

      it("should apply the bitmask to the IPv6 address according to sourcePrefixLength") {
        testBitmask(ClientSubnetOPTOptionData.familyIPv6)
      }
    }

    describe("UnknownOPTOptionData") {
      it("decode(encode(resource)) should be the same as resource") {
        forAll(bytesGenerator()) { bytes =>
          val r       = OPTResource(OPTResource.OPTOption(1, OPTResource.UnknownOPTOptionData(bytes)) :: Nil)
          val encoded = r(MessageBuffer()).flipped()

          OPTResource(encoded, encoded.remaining()).options match {
            case OPTResource.OPTOption(1, OPTResource.UnknownOPTOptionData(b)) :: Nil =>
              assert(b === bytes)
            case _ => fail()
          }
        }
      }
    }
  }
}
