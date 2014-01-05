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
import com.github.mkroli.dns4s.section.QuestionSection
import com.github.mkroli.dns4s.section.ResourceRecord

object ~ {
  def unapply[T](t: T): Option[(T, T)] = Some(t, t)
}

trait MessageModifier { self =>
  def ~(mm: MessageModifier) = new MessageModifier {
    override def apply(m: Message): Message = mm(self(m))
  }

  def apply(m: Message): Message
}

private[dsl] abstract class DnsType(t: Int) extends QuestionSectionModifier with ResourceRecordModifier {
  override def apply(qs: QuestionSection) = qs.copy(qtype = t)

  override def apply(rr: ResourceRecord) = rr.copy(`type` = t)

  def unapply(qs: QuestionSection): Option[Unit] = qs.qtype == t

  def unapply(rr: ResourceRecord): Option[Unit] = rr.`type` == t
}

object TypeA extends DnsType(1)
object TypeNS extends DnsType(2)
object TypeMD extends DnsType(3)
object TypeMF extends DnsType(4)
object TypeCNAME extends DnsType(5)
object TypeSOA extends DnsType(6)
object TypeMB extends DnsType(7)
object TypeMG extends DnsType(8)
object TypeMR extends DnsType(9)
object TypeNULL extends DnsType(10)
object TypeWKS extends DnsType(11)
object TypePTR extends DnsType(12)
object TypeHINFO extends DnsType(13)
object TypeMINFO extends DnsType(14)
object TypeMX extends DnsType(15)
object TypeTXT extends DnsType(16)
object TypeAAAA extends DnsType(28)
object TypeAXFR extends DnsType(252)
object TypeMAILB extends DnsType(253)
object TypeMAILA extends DnsType(254)
object TypeAsterisk extends DnsType(255)

private[dsl] abstract class DnsClass(c: Int) extends QuestionSectionModifier with ResourceRecordModifier {
  override def apply(qs: QuestionSection) = qs.copy(qclass = c)

  override def apply(rr: ResourceRecord) = rr.copy(`class` = c)

  def unapply(qs: QuestionSection): Option[Unit] = qs.qclass == c

  def unapply(rr: ResourceRecord): Option[Unit] = rr.`class` == c
}

object ClassIN extends DnsClass(1)
object ClassCS extends DnsClass(2)
object ClassCH extends DnsClass(3)
object ClassHS extends DnsClass(4)
object ClassAsterisk extends DnsClass(255)
