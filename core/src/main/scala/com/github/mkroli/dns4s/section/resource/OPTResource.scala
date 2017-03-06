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

import com.github.mkroli.dns4s.{MessageBuffer, MessageBufferEncoder}
import com.github.mkroli.dns4s.section.Resource

case class OPTResource(options: List[OPTResource.OPTOption]) extends Resource {
  override def apply(buf: MessageBuffer) = {
    options.foldLeft(buf) { (buf, option) =>
      option(buf)
    }
  }
}

object OPTResource {
  val optionClientSubnet = 8

  private def readOptions(buf: MessageBuffer, remainingBytes: Int, options: List[OPTOption]): List[OPTOption] = {
    if (remainingBytes < 4) {
      options
    } else {
      val optionCode = buf.getUnsignedInt(2)
      val optionLength = math.min(remainingBytes, buf.getUnsignedInt(2))
      val optionData = optionCode match {
        case `optionClientSubnet` =>
          ClientSubnetOPTOptionData(buf, optionLength)
        case _ =>
          UnknownOPTOptionData(buf, optionLength)
      }
      readOptions(buf, remainingBytes - 4 - optionLength, OPTOption(optionCode, optionData) :: options)
    }
  }

  def apply(buf: MessageBuffer, length: Int) = {
    val options = readOptions(buf, length, Nil)
    new OPTResource(options)
  }

  trait OPTOptionData extends MessageBufferEncoder

  case class OPTOption(code: Int, data: OPTOptionData) extends MessageBufferEncoder {
    override def apply(buf: MessageBuffer) = {
      buf.putUnsignedInt(2, code)
        .putLengthOf(2, data.apply)
    }
  }

  case class ClientSubnetOPTOptionData(family: Int, sourcePrefixLength: Int, scopePrefixLength: Int, address: InetAddress) extends OPTOptionData {
    override def apply(buf: MessageBuffer) = {
      val addressLength = math.ceil(sourcePrefixLength / 8.0).toInt
      val addressBytes = address.getAddress.take(addressLength)
      val mask: Byte = ((1 << (sourcePrefixLength % 8)) - 1).toByte
      val lastByte: Byte = addressBytes(addressBytes.length - 1)
      val lastBytePadded: Byte = (lastByte & mask).toByte
      addressBytes(addressBytes.length - 1) = lastBytePadded
      buf
        .putUnsignedInt(2, family)
        .putUnsignedInt(1, sourcePrefixLength)
        .putUnsignedInt(1, scopePrefixLength)
        .put(addressBytes)
    }
  }

  object ClientSubnetOPTOptionData {
    val familyIPv4 = 1
    val familyIPv6 = 2

    def apply(buf: MessageBuffer, optionLength: Int): ClientSubnetOPTOptionData = {
      val family = buf.getUnsignedInt(2)
      val sourcePrefixLength = buf.getUnsignedInt(1)
      val scopePrefixLength = buf.getUnsignedInt(1)
      val addressDataLength = optionLength - 4
      val addressDataBytes = buf.getBytes(addressDataLength)
      val familyLength = family match {
        case `familyIPv4` => 4
        case `familyIPv6` => 16
      }
      val paddingBytes = Vector.fill(familyLength - addressDataLength)(0: Byte)
      val address = InetAddress.getByAddress((addressDataBytes ++ paddingBytes).toArray)
      ClientSubnetOPTOptionData(family, sourcePrefixLength, scopePrefixLength, address)
    }
  }

  case class UnknownOPTOptionData(bytes: Array[Byte]) extends OPTOptionData {
    override def apply(buf: MessageBuffer) = {
      buf.put(bytes)
    }
  }

  object UnknownOPTOptionData {
    def apply(buf: MessageBuffer, optionLength: Int): UnknownOPTOptionData = {
      UnknownOPTOptionData(buf.getBytes(optionLength).toArray)
    }
  }

}
