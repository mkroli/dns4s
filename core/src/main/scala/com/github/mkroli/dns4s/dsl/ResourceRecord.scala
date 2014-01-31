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

import java.net.Inet4Address
import java.net.InetAddress

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.section.ResourceRecord
import com.github.mkroli.dns4s.section.resource.AResource
import com.github.mkroli.dns4s.section.resource.CNameResource
import com.github.mkroli.dns4s.section.resource.MXResource
import com.github.mkroli.dns4s.section.resource.NSResource
import com.github.mkroli.dns4s.section.resource.PTRResource
import com.github.mkroli.dns4s.section.resource.SOAResource

trait ResourceRecordModifier { self =>
  def ~(rrm: ResourceRecordModifier) = new ResourceRecordModifier {
    override def apply(rr: ResourceRecord): ResourceRecord = rrm(self(rr))
  }

  def apply(rr: ResourceRecord): ResourceRecord
}

private[dsl] abstract class ResourceRecordSection(set: (Message, Seq[ResourceRecord]) => Message, get: Message => Seq[ResourceRecord]) {
  def apply(rr: ResourceRecord*) = new MessageModifier {
    override def apply(msg: Message) = {
      val msgcp = set(msg, get(msg) ++ rr)
      msgcp.copy(header = msgcp.header.copy(
        ancount = msgcp.answer.size,
        nscount = msgcp.authority.size,
        arcount = msgcp.additional.size))
    }
  }

  def apply(msg: Message): MessageModifier = apply(get(msg): _*)

  def unapply(msg: Message): Option[Seq[ResourceRecord]] = Some(get(msg).toList)
}

object Answers extends ResourceRecordSection((msg, rr) => msg.copy(answer = rr), _.answer)
object Authority extends ResourceRecordSection((msg, rr) => msg.copy(authority = rr), _.authority)
object Additional extends ResourceRecordSection((msg, rr) => msg.copy(additional = rr), _.additional)

private[dsl] abstract class ResourceRecordField[T](e: ResourceRecord => T) {
  def unapply(rr: ResourceRecord): Option[T] = Some(e(rr))
}

object RRName extends ResourceRecordField(_.name)
object RRType extends ResourceRecordField(_.`type`)
object RRClass extends ResourceRecordField(_.`class`)
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

object ARecord extends ResourceRecordExtractor[AResource] {
  def apply(addr: Inet4Address): ResourceRecordModifier =
    resourceRecordModifier(ResourceRecord.typeA, AResource(addr))

  def apply(addr: Array[Byte]): ResourceRecordModifier = {
    ARecord(InetAddress.getByAddress(addr) match {
      case addr: Inet4Address => addr
      case _ => throw new RuntimeException
    })
  }

  def apply(addr: String): ResourceRecordModifier =
    ARecord(addr.split("""\.""").map(_.toByte))
}

object CNameRecord extends ResourceRecordExtractor[CNameResource] {
  def apply(name: String) =
    resourceRecordModifier(ResourceRecord.typeCNAME, CNameResource(name))
}

object MXRecord extends ResourceRecordExtractor[MXResource] {
  def apply(preference: Int, exchange: String) =
    resourceRecordModifier(ResourceRecord.typeMX, MXResource(preference, exchange))
}

object NSRecord extends ResourceRecordExtractor[NSResource] {
  def apply(nsdname: String) =
    resourceRecordModifier(ResourceRecord.typeNS, NSResource(nsdname))
}

object PTRRecord extends ResourceRecordExtractor[PTRResource] {
  def apply(ptrdname: String) =
    resourceRecordModifier(ResourceRecord.typePTR, PTRResource(ptrdname))
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
