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
package com.github.mkroli.dns4s.section.resource

import java.net.InetAddress

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.section.Resource

case class OPTResource(optionCode: Int, optionLength: Int,
                       addressFamily: Int, netmask: Int,
                       netmaskScope: Int = 0, inetAddress: String) extends Resource {
  def apply(buf: MessageBuffer): MessageBuffer = buf.putUnsignedInt(optionCode, 2)
    .putUnsignedInt(optionLength, 2).putUnsignedInt(2, addressFamily)
    .putUnsignedInt(1, netmask).putUnsignedInt(1, netmaskScope).putCharacterString(inetAddress)

}

object OPTResource {
  def apply(buf: MessageBuffer, length: Int): OPTResource = {


    val optionCode = buf.getUnsignedInt(2)
    val optionLength = buf.getUnsignedInt(2)
    val addressFamily = buf.getUnsignedInt(2)
    val netmask = buf.getUnsignedInt(1)
    val netmaskScope = buf.getUnsignedInt(1)

    val inetAddr = {
      if (addressFamily == 1) {
        InetAddress.getByAddress(buf.getBytes(4).toArray)
      }
      else {
        InetAddress.getByAddress(buf.getBytes(length - 8).toArray)
      }
    }.getHostAddress

    new OPTResource(optionCode, optionLength, addressFamily, netmask, netmaskScope, inetAddr)
  }
}
