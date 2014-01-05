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
package com.github.mkroli.dns4s.dsl

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.section.HeaderSection

object Id {
  def apply(id: Int) = new MessageModifier {
    override def apply(msg: Message) = msg.copy(msg.header.copy(id = id))
  }

  def unapply(msg: Message): Option[Int] = Some(msg.header.id)
}

private[dsl] trait DnsQr { self: Message =>
  def unapply(msg: Message) = if (msg.header.qr == self.header.qr) Some(msg) else None
}

object Query extends PlainMessage(false) with DnsQr
object Response extends PlainMessage(true) with DnsQr

private[dsl] abstract class DnsFlag(set: HeaderSection => HeaderSection, get: (HeaderSection) => Boolean) extends MessageModifier {
  override def apply(msg: Message) = msg.copy(header = set(msg.header))

  def unapply(msg: Message): Option[Unit] = get(msg.header)
}
private[dsl] abstract class DnsOpcode(opcode: Int) extends DnsFlag(_.copy(opcode = opcode), _.opcode == opcode)
private[dsl] abstract class DnsRcode(rcode: Int) extends DnsFlag(_.copy(rcode = rcode), _.rcode == rcode)

object StandardQuery extends DnsOpcode(HeaderSection.opcodeStandardQuery)
object InverseQuery extends DnsOpcode(HeaderSection.opcodeInverseQuery)
object ServerStatusRequest extends DnsOpcode(HeaderSection.opcodeServerStatusRequest)

object AuthoritativeAnswer extends DnsFlag(_.copy(aa = true), _.aa)
object Truncation extends DnsFlag(_.copy(tc = true), _.tc)
object RecursionDesired extends DnsFlag(_.copy(rd = true), _.rd)
object RecursionAvailable extends DnsFlag(_.copy(ra = true), _.ra)

object NoError extends DnsRcode(HeaderSection.rcodeNoError)
object FormatError extends DnsRcode(HeaderSection.rcodeFormatError)
object ServerFailure extends DnsRcode(HeaderSection.rcodeServerFailure)
object NameError extends DnsRcode(HeaderSection.rcodeNameError)
object NotImplemented extends DnsRcode(HeaderSection.rcodeNotImplemented)
object Refused extends DnsRcode(HeaderSection.rcodeRefused)
