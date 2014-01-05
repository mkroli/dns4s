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

case class SOAResource(
  mname: String,
  rname: String,
  serial: Long,
  refresh: Long,
  retry: Long,
  expire: Long,
  minimum: Long) extends Resource {
  require(serial >= 0 && serial < (1L << 32))
  require(refresh >= 0 && refresh < (1L << 32))
  require(retry >= 0 && retry < (1L << 32))
  require(expire >= 0 && expire < (1L << 32))
  require(minimum >= 0 && minimum < (1L << 32))

  def apply(buf: MessageBuffer) = buf
    .putDomainName(mname)
    .putDomainName(rname)
    .putUnsignedLong(4, serial)
    .putUnsignedLong(4, refresh)
    .putUnsignedLong(4, retry)
    .putUnsignedLong(4, expire)
    .putUnsignedLong(4, minimum)
}

object SOAResource {
  def apply(buf: MessageBuffer) = new SOAResource(
    mname = buf.getDomainName,
    rname = buf.getDomainName,
    serial = buf.getUnsignedLong(4),
    refresh = buf.getUnsignedLong(4),
    retry = buf.getUnsignedLong(4),
    expire = buf.getUnsignedLong(4),
    minimum = buf.getUnsignedLong(4))
}
