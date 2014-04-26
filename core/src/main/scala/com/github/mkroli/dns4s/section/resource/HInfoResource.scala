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

case class HInfoResource(cpu: String, os: String) extends Resource {
  def apply(buf: MessageBuffer) =
    buf.putCharacterString(cpu).putCharacterString(os)
}

object HInfoResource {
  def apply(buf: MessageBuffer) =
    new HInfoResource(buf.getCharacterString, buf.getCharacterString)
}
