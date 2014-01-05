/*
 * Copyright 2013 Michael Krolikowski
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

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.MessageBufferEncoder
import com.github.mkroli.dns4s.section.resource.AResource
import com.github.mkroli.dns4s.section.resource.CNameResource
import com.github.mkroli.dns4s.section.resource.MXResource
import com.github.mkroli.dns4s.section.resource.NSResource
import com.github.mkroli.dns4s.section.resource.PTRResource
import com.github.mkroli.dns4s.section.resource.SOAResource
import com.github.mkroli.dns4s.section.resource.UnknownResource

case class ResourceRecord(
  name: String,
  `type`: Int,
  `class`: Int,
  ttl: Long,
  rdata: Resource) extends MessageBufferEncoder {
  require(`type` >= 0 && `type` < (1 << 16))
  require(`class` >= 0 && `class` < (1 << 16))
  require(ttl >= 0 && ttl < (1L << 32))

  def apply(buf: MessageBuffer) = {
    buf
      .putDomainName(name)
      .putUnsignedInt(2, `type`)
      .putUnsignedInt(2, `class`)
      .putUnsignedLong(4, ttl)
      .putLengthOf(2, rdata.apply)
  }
}

object ResourceRecord {
  val typeA = 1
  val typeNS = 2
  val typeMD = 3
  val typeMF = 4
  val typeCNAME = 5
  val typeSOA = 6
  val typeMB = 7
  val typeMG = 8
  val typeMR = 9
  val typeNULL = 10
  val typeWKS = 11
  val typePTR = 12
  val typeHINFO = 13
  val typeMINFO = 14
  val typeMX = 15
  val typeTXT = 16
  val typeAAAA = 28
  val qtypeAXFR = 252
  val qtypeMAILB = 253
  val qtypeMAILA = 254
  val qtypeAsterisk = 255

  val classIN = 1
  val classCS = 2
  val classCH = 3
  val classHS = 4
  val qclassAsterisk = 255

  def apply(buf: MessageBuffer) = {
    val name = buf.getDomainName()
    val `type` = buf.getUnsignedInt(2)
    val `class` = buf.getUnsignedInt(2)
    val ttl = buf.getUnsignedLong(4)
    val rdlength = buf.getUnsignedInt(2)
    val rdata = `type` match {
      case 1 => AResource(buf)
      case 2 => NSResource(buf)
      case 5 => CNameResource(buf)
      case 6 => SOAResource(buf)
      case 12 => PTRResource(buf)
      case 15 => MXResource(buf)
      case _ => UnknownResource(buf, rdlength, `type`)
    }
    new ResourceRecord(name, `type`, `class`, ttl, rdata)
  }
}
