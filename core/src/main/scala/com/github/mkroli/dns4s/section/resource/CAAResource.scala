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

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.section.Resource

sealed abstract class CAAResource(tag: String,
                                  valueBytes: Array[Byte],
                                  flagsByte: Byte)
    extends Resource {

  require(tag.nonEmpty, "tag should not be empty")

  def apply(buf: MessageBuffer): MessageBuffer = {
    buf
      .put(flagsByte)
      .putCharacterString(tag)
      .put(valueBytes)
  }
}

object CAAResource {

  private val issue = "issue"
  private val issuewild = "issuewild"
  private val iodef = "iodef"

  def apply(buf: MessageBuffer, rdLength: Int): CAAResource = {
    val pos = buf.buf.position()
    val flagByte = buf.get()
    val tag = buf.getCharacterString()
    val valueLength = rdLength - buf.buf.position() + pos
    def getValue: String = buf.getString(valueLength)
    val issuerCritical: Boolean = flagByte == 1

    tag match {
      case `issue`     => IssueResource(getValue, issuerCritical)
      case `issuewild` => IssueWildResource(getValue, issuerCritical)
      case `iodef`     => IODEFResource(getValue)
      case unknownTag =>
        CustomCAAResource(
          unknownTag,
          buf.getBytes(valueLength).toArray,
          flagByte
        )
    }
  }

  private def createFlagByte(issuerCritical: Boolean): Byte =
    if (issuerCritical) 1 else 0

  case class IssueResource(value: String, issuerCritical: Boolean)
      extends CAAResource(issue, value.getBytes, createFlagByte(issuerCritical))

  case class IssueWildResource(value: String, issuerCritical: Boolean)
      extends CAAResource(
        issuewild,
        value.getBytes,
        createFlagByte(issuerCritical)
      )

  case class IODEFResource(value: String)
      extends CAAResource(iodef, value.getBytes, createFlagByte(false))

  case class CustomCAAResource(tag: String,
                               valueBytes: Array[Byte],
                               flagsByte: Byte)
      extends CAAResource(tag, valueBytes, flagsByte) {

    override def equals(obj: Any): Boolean = {
      obj match {
        case that: CustomCAAResource =>
          tag == that.tag &&
            (valueBytes sameElements that.valueBytes) &&
            flagsByte == that.flagsByte
        case _ => false
      }
    }
  }
}
