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
package com.github.mkroli.dns4s.section.resource

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.section.Resource

case class MXResource(preference: Int, exchange: String) extends Resource {
  require(preference >= 0 && preference < (1 << 16))

  def apply(buf: MessageBuffer) =
    buf.putUnsignedInt(2, preference).putDomainName(exchange)
}

object MXResource {
  def apply(buf: MessageBuffer) =
    new MXResource(buf.getUnsignedInt(2), buf.getDomainName)
}
