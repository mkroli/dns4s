/*
 * Copyright 2013-2017 Michael Krolikowski
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

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.section.resource.{
  AAAAResource,
  AResource,
  CAAResource,
  CNameResource,
  HInfoResource,
  MXResource,
  NAPTRResource,
  NSResource,
  OPTResource,
  PTRResource,
  SOAResource,
  TXTResource
}
import com.github.mkroli.dns4s.section.resource.CAAResource.{
  IODEFResource,
  IssueResource,
  IssueWildResource,
  CustomCAAResource
}
import com.github.mkroli.dns4s.section.resource._
import com.google.common.net.InetAddresses

trait ResourceRecordModifier { self =>
  def ~(rrm: ResourceRecordModifier) = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord): ResourceRecord = rrm(self(rr))
  }

  def apply(rr: ResourceRecord): ResourceRecord
}

private[dsl] abstract class ResourceRecordSection(set: (Message, Seq[ResourceRecord]) => Message, get: Message => Seq[ResourceRecord]) {
  def apply[T](rr: T*)(implicit toResourceRecord: (T) => ResourceRecord): MessageModifier = {
    apply(append = true, rr: _*)
  }

  def apply[T](append: Boolean, rr: T*)(implicit toResourceRecord: (T) => ResourceRecord): MessageModifier = new MessageModifier {
    override def apply(msg: Message) = {
      val rrs = if(append) get(msg) ++ rr.map(toResourceRecord) else rr.map(toResourceRecord)
      val msgcp = set(msg, rrs)
      msgcp.copy(header = msgcp.header.copy(
        ancount = msgcp.answer.size,
        nscount = msgcp.authority.size,
        arcount = msgcp.additional.size))
    }
  }

  def unapply(msg: Message): Option[Seq[ResourceRecord]] = Some(get(msg).toList)
}

object Answers extends ResourceRecordSection((msg, rr) => msg.copy(answer = rr), _.answer)
object Authority extends ResourceRecordSection((msg, rr) => msg.copy(authority = rr), _.authority)
object Additional extends ResourceRecordSection((msg, rr) => msg.copy(additional = rr), _.additional)

private[dsl] abstract class ResourceRecordField[T](e: ResourceRecord => T) {
  def unapply(rr: ResourceRecord): Option[T] = Some(e(rr))
}

object RRName extends ResourceRecordField(_.name) {
  def apply(name: String): ResourceRecordModifier = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord) = rr.copy(name = name)
  }
}

object RRType extends ResourceRecordField(_.`type`) {
  def apply(`type`: Int): ResourceRecordModifier = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord) = rr.copy(`type` = `type`)
  }
}

object RRClass extends ResourceRecordField(_.`class`) {
  def apply(`class`: Int): ResourceRecordModifier = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord) = rr.copy(`class` = `class`)
  }
}

object RRTtl extends ResourceRecordField(_.`ttl`) {
  def apply(ttl: Long) = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord) = rr.copy(ttl = ttl)
  }
}

private[dsl] abstract class ResourceRecordExtractor[T: Manifest] {
  def unapply(rr: ResourceRecord): Option[T] = rr.rdata match {
    case rr: T => Some(rr)
    case _ => None
  }
}

object EDNS {
  def apply(payloadSize: Int = 4096): MessageModifier = new MessageModifier {
    override def apply(msg: Message) = {
      Additional(ResourceRecord("", ResourceRecord.typeOPT, payloadSize, 0, OPTResource(Nil))).apply(msg)
    }
  }

  def unapply(msg: Message): Option[Int] = msg.additional.collectFirst {
    case ResourceRecord(_, ResourceRecord.typeOPT, payloadSize, _, _) => payloadSize
  }
}

object ARecord extends ResourceRecordExtractor[AResource] {
  def apply(addr: Inet4Address): ResourceRecordModifier =
    resourceRecordModifier(ResourceRecord.typeA, AResource(addr))

  def apply(addr: Array[Byte]): ResourceRecordModifier = {
    ARecord(InetAddress.getByAddress(addr) match {
      case addr: Inet4Address => addr
      case _ => throw new RuntimeException
    })
  }

  def apply(addr: String): ResourceRecordModifier = {
    InetAddresses.forString(addr) match {
      case addr: Inet4Address => ARecord(addr)
      case _ => throw new RuntimeException
    }
  }
}

object AAAARecord extends ResourceRecordExtractor[AAAAResource] {
  def apply(addr: Inet6Address): ResourceRecordModifier =
    resourceRecordModifier(ResourceRecord.typeAAAA, AAAAResource(addr))

  def apply(addr: Array[Byte]): ResourceRecordModifier = {
    AAAARecord(InetAddress.getByAddress(addr) match {
      case addr: Inet6Address => addr
      case _ => throw new RuntimeException
    })
  }

  def apply(addr: String): ResourceRecordModifier = {
    InetAddresses.forString(addr) match {
      case addr: Inet6Address => AAAARecord(addr)
      case _ => throw new RuntimeException
    }
  }
}

object CNameRecord extends ResourceRecordExtractor[CNameResource] {
  def apply(name: String) =
    resourceRecordModifier(ResourceRecord.typeCNAME, CNameResource(name))
}

object MXRecord extends ResourceRecordExtractor[MXResource] {
  def apply(preference: Int, exchange: String) =
    resourceRecordModifier(ResourceRecord.typeMX, MXResource(preference, exchange))
}

object NAPTRRecord extends ResourceRecordExtractor[NAPTRResource] {
  def apply(order: Int, preference: Int, flags: String, services: String, regexp: String, replacement: String) =
    resourceRecordModifier(ResourceRecord.typeNAPTR, NAPTRResource(order, preference, flags, services, regexp, replacement))
}

object OPTRecord extends ResourceRecordExtractor[OPTResource] {
  def apply(options: List[OPTResource.OPTOption] = Nil) =
    resourceRecordModifier(ResourceRecord.typeOPT, OPTResource(options))
}

object NSRecord extends ResourceRecordExtractor[NSResource] {
  def apply(nsdname: String) =
    resourceRecordModifier(ResourceRecord.typeNS, NSResource(nsdname))
}

object PTRRecord extends ResourceRecordExtractor[PTRResource] {
  def apply(ptrdname: String) =
    resourceRecordModifier(ResourceRecord.typePTR, PTRResource(ptrdname))
}

object HInfoRecord extends ResourceRecordExtractor[HInfoResource] {
  def apply(cpu: String, os: String) =
    resourceRecordModifier(ResourceRecord.typeHINFO, HInfoResource(cpu, os))
}

object CAARecord extends ResourceRecordExtractor[CAAResource] {
  
  object Issue extends ResourceRecordExtractor[IssueResource] {
    def apply(value: String, issuerCritical: Boolean): ResourceRecordModifier =
      resourceRecordModifier(
        ResourceRecord.typeCAA,
        IssueResource(value, issuerCritical)
      )
  }

  object IssueWild extends ResourceRecordExtractor[IssueWildResource] {
    def apply(value: String, issuerCritical: Boolean): ResourceRecordModifier =
      resourceRecordModifier(
        ResourceRecord.typeCAA,
        IssueWildResource(value, issuerCritical)
      )
  }

  object IODEF extends ResourceRecordExtractor[IODEFResource] {
    def apply(value: String): ResourceRecordModifier =
      resourceRecordModifier(ResourceRecord.typeCAA, IODEFResource(value))
  }

  object Custom extends ResourceRecordExtractor[CustomCAAResource] {
    def apply(tag: String,
              value: Array[Byte],
              flags: Byte): ResourceRecordModifier =
      resourceRecordModifier(
        ResourceRecord.typeCAA,
        CustomCAAResource(tag, value, flags)
      )
  }
}

object TXTRecord extends ResourceRecordExtractor[TXTResource] {
  def apply(txt: String*) =
    resourceRecordModifier(ResourceRecord.typeTXT, TXTResource(txt))
}

object SOARecord extends ResourceRecordExtractor[SOAResource] {
  def apply(mname: String,
    rname: String,
    serial: Long,
    refresh: Long,
    retry: Long,
    expire: Long,
    minimum: Long) =
    resourceRecordModifier(ResourceRecord.typeSOA,
      SOAResource(mname, rname, serial, refresh, retry, expire, minimum))
}
