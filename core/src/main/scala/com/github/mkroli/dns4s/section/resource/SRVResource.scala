/*
  Copyright 2015 Michael Krolikowski, Peter van Rensburg

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.github.mkroli.dns4s.section.resource

import com.github.mkroli.dns4s.MessageBuffer
import com.github.mkroli.dns4s.section.Resource

case class SRVResource(priority: Int, weight: Int, port: Int, target: String) extends Resource {
  require(priority >= 0 && priority < (1 << 16))
  require(weight >= 0 && weight < (1 << 16))
  require(port >= 0 && port < (1 << 16))

  def apply(buf: MessageBuffer) = buf.putUnsignedInt(2, priority).putUnsignedInt(2, weight).putUnsignedInt(2, port).putDomainName(target)
}

object SRVResource {
  def apply(buf: MessageBuffer) =
    new SRVResource(buf.getUnsignedInt(2), buf.getUnsignedInt(2), buf.getUnsignedInt(2), buf.getDomainName)
}
