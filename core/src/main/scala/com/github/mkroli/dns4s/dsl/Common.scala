/*
 * Copyright 2013-2015 Michael Krolikowski
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

class Contained[A, B](u: A => Option[B]) {
  def unapply(i: Iterable[A]): Option[B] = i.map(u).collectFirst {
    case Some(x) => x
  }
}

trait ContainedMatcher[A, B] {
  val contained = new Contained[A, B](unapply _)

  def unapply(a: A): Option[B]
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

  def unapply(qs: QuestionSection) = qs.qtype == t

  def unapply(rr: ResourceRecord) = rr.`type` == t
}

object TypeA extends DnsType(ResourceRecord.typeA)
object TypeNS extends DnsType(ResourceRecord.typeNS)
object TypeMD extends DnsType(ResourceRecord.typeMD)
object TypeMF extends DnsType(ResourceRecord.typeMF)
object TypeCNAME extends DnsType(ResourceRecord.typeCNAME)
object TypeSOA extends DnsType(ResourceRecord.typeSOA)
object TypeMB extends DnsType(ResourceRecord.typeMB)
object TypeMG extends DnsType(ResourceRecord.typeMG)
object TypeMR extends DnsType(ResourceRecord.typeMR)
object TypeNULL extends DnsType(ResourceRecord.typeNULL)
object TypeWKS extends DnsType(ResourceRecord.typeWKS)
object TypePTR extends DnsType(ResourceRecord.typePTR)
object TypeHINFO extends DnsType(ResourceRecord.typeHINFO)
object TypeMINFO extends DnsType(ResourceRecord.typeMINFO)
object TypeMX extends DnsType(ResourceRecord.typeMX)
object TypeTXT extends DnsType(ResourceRecord.typeTXT)
object TypeAAAA extends DnsType(ResourceRecord.typeAAAA)
object TypeSRV extends DnsType(ResourceRecord.typeSRV)
object TypeNAPTR extends DnsType(ResourceRecord.typeNAPTR)
object TypeOPT extends DnsType(ResourceRecord.typeOPT)
object TypeAXFR extends DnsType(ResourceRecord.qtypeAXFR)
object TypeMAILB extends DnsType(ResourceRecord.qtypeMAILB)
object TypeMAILA extends DnsType(ResourceRecord.qtypeMAILA)
object TypeAsterisk extends DnsType(ResourceRecord.qtypeAsterisk)

object DnsTypeName {
  private val dnsTypeToString: PartialFunction[Int, String] = {
    case 1 => "A"
    case 2 => "NS"
    case 3 => "MD"
    case 4 => "MF"
    case 5 => "CNAME"
    case 6 => "SOA"
    case 7 => "MB"
    case 8 => "MG"
    case 9 => "MR"
    case 10 => "NULL"
    case 11 => "WKS"
    case 12 => "PTR"
    case 13 => "HINFO"
    case 14 => "MINFO"
    case 15 => "MX"
    case 16 => "TXT"
    case 28 => "AAAA"
    case 33 => "SRV"
    case 35 => "NAPTR"
    case 41 => "OPT"
    case 252 => "AXFR"
    case 253 => "MAILB"
    case 254 => "MAILA"
    case 255 => "Asterisk"
  }

  def unapply(qs: QuestionSection): Option[String] = dnsTypeToString.lift(qs.qtype)

  def unapply(rr: ResourceRecord): Option[String] = dnsTypeToString.lift(rr.`type`)
}

private[dsl] abstract class DnsClass(c: Int) extends QuestionSectionModifier with ResourceRecordModifier {
  override def apply(qs: QuestionSection) = qs.copy(qclass = c)

  override def apply(rr: ResourceRecord) = rr.copy(`class` = c)

  def unapply(qs: QuestionSection) = qs.qclass == c

  def unapply(rr: ResourceRecord) = rr.`class` == c
}

object ClassIN extends DnsClass(1)
object ClassCS extends DnsClass(2)
object ClassCH extends DnsClass(3)
object ClassHS extends DnsClass(4)
object ClassAsterisk extends DnsClass(255)

object DnsClassName {
  private val dnsClassToString: PartialFunction[Int, String] = {
    case 1 => "IN"
    case 2 => "CS"
    case 3 => "CH"
    case 4 => "HS"
    case 255 => "Asterisk"
  }

  def unapply(qs: QuestionSection) = dnsClassToString.lift(qs.qclass)

  def unapply(rr: ResourceRecord) = dnsClassToString.lift(rr.`class`)
}
