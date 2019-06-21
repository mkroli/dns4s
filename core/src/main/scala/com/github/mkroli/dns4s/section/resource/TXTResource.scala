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

case class TXTResource(txt: Seq[String]) extends Resource {
  def apply(buf: MessageBuffer) =
    txt.foldLeft(buf)(_ putCharacterString _)
}

object TXTResource {
  def apply(buf: MessageBuffer, size: Int) = {
    def getCharacterStrings(cs: Seq[String], updatedCsSize: Int): Seq[String] = {
      if (updatedCsSize < size) {
        val str = buf.getCharacterString
        val updatedCs = cs :+ str
        getCharacterStrings(updatedCs, updatedCsSize + str.getBytes.length + 1)
      } else
        cs
    }
    new TXTResource(getCharacterStrings(Nil, 0))
  }
}
