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

case class QuestionSection(
  qname: String,
  qtype: Int,
  qclass: Int) extends MessageBufferEncoder {
  require(qtype >= 0 && qtype < (1 << 16))
  require(qclass >= 0 && qclass < (1 << 16))

  def apply(buf: MessageBuffer) = {
    buf
      .putDomainName(qname)
      .putUnsignedInt(2, qtype)
      .putUnsignedInt(2, qclass)
  }
}

object QuestionSection {
  def apply(buf: MessageBuffer) = {
    new QuestionSection(
      buf.getDomainName(),
      buf.getUnsignedInt(2),
      buf.getUnsignedInt(2))
  }
}
