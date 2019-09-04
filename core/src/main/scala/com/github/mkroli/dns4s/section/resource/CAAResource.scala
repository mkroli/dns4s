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

/**
  * Certification Authority Authorization.
  * A CAA RR contains a single property entry consisting of a tag-value
  * pair.  Each tag represents a property of the CAA record.  The value
  * of a CAA property is that specified in the corresponding value field.
  *
  * @see https://tools.ietf.org/html/rfc6844#section-5.1
  *
  * @param flag  One octet containing Issuer Critical Flag. If the value is set to '1', the
  *              critical flag is asserted and the property MUST be understood
  *              if the CAA record is to be correctly processed by a certificate
  *              issuer.
  * @param tag   The property identifier, a sequence of US-ASCII characters.
  *              Tag values MAY contain US-ASCII characters 'a' through 'z', 'A'
  *              through 'Z', and the numbers 0 through 9.  Tag values SHOULD NOT
  *              contain any other characters.  Matching of tag values is case
  *              insensitive.
  * @param value A sequence of octets representing the property value.
  *              Property values are encoded as binary values and MAY employ sub-
  *              formats.
  *              The length of the value field is specified implicitly as the
  *              remaining length of the enclosing Resource Record data field.
  */
case class CAAResource(flag: Int, tag: String, value: String) extends Resource {
  require(flag >= 0 && flag <= 255, "flag value should be >= 0 and <= 255")
  require(tag.nonEmpty, "tag should not be empty")

  def apply(buf: MessageBuffer): MessageBuffer = {
    val tagLength = tag.getBytes.length
    buf
      .putUnsignedInt(1, flag)
      .putUnsignedInt(1, tagLength)
      .putBytes(tagLength, tag.getBytes)
      .put(value.getBytes)
  }
}

object CAAResource {
  def apply(buf: MessageBuffer): CAAResource = {
    val (flag, tagLength) = (buf.getUnsignedInt(1), buf.getUnsignedInt(1))
    val tagString = byteArrayToString(buf.getBytes(tagLength))
    val valueString = byteArrayToString(buf.getBytes(buf.remaining()))
    new CAAResource(flag, tagString, valueString)
  }

  private def byteArrayToString(byteArray: IndexedSeq[Byte]) =
    byteArray.map(_.toChar).mkString
}
