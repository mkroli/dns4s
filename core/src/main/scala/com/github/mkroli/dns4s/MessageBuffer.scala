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
package com.github.mkroli.dns4s

import java.nio.ByteBuffer

import scala.language.postfixOps

trait MessageBufferEncoder {
  def apply(buf: MessageBuffer): MessageBuffer
}

class MessageBuffer private (val buf: ByteBuffer, val domains: Map[String, Int]) {
  def put(a: Array[Byte]) = new MessageBuffer(buf.put(a), domains)

  def put(b: Byte) = new MessageBuffer(buf.put(b), domains)

  def flipped() = {
    buf.flip()
    this
  }

  def remaining() = buf.remaining()

  def get() = buf.get()

  def getBytes(bytes: Int) = {
    require(bytes >= 0)
    (0 until bytes).map(_ => buf.get())
  }

  def putBytes(bytes: Int, a: Array[Byte]) = new MessageBuffer({
    require(a.length <= bytes)
    (0 until (bytes - a.length)).foreach(_ => buf.put(0: Byte))
    buf.put(a)
  }, domains)

  def getSignedBigInt(bytes: Int) =
    BigInt(getBytes(bytes) toArray)

  def putSignedBigInt(bytes: Int, i: BigInt) =
    putBytes(bytes, i.toByteArray)

  def getUnsignedBigInt(bytes: Int) =
    BigInt((0: Byte) +: getBytes(bytes) toArray)

  def putUnsignedBigInt(bytes: Int, i: BigInt) = {
    putBytes(bytes, i.toByteArray.toList match {
      case 0 :: tail => tail.toArray
      case a => a.toArray
    })
  }

  def getSignedLong(bytes: Int) = {
    require(bytes > 0 && bytes <= 8)
    getSignedBigInt(bytes).toLong
  }

  def putSignedLong(bytes: Int, l: Long) = {
    require(bytes > 0 && bytes <= 8)
    putSignedBigInt(bytes, l)
  }

  def getUnsignedLong(bytes: Int) = {
    require(bytes > 0 && bytes < 8)
    getUnsignedBigInt(bytes).toLong
  }

  def putUnsignedLong(bytes: Int, l: Long) = {
    require(bytes > 0 && bytes < 8)
    putUnsignedBigInt(bytes, l)
  }

  def getSignedInt(bytes: Int) = {
    require(bytes > 0 && bytes <= 4)
    getSignedBigInt(bytes).toInt
  }

  def putSignedInt(bytes: Int, i: Int) = {
    require(bytes > 0 && bytes <= 4)
    putSignedBigInt(bytes, i)
  }

  def getUnsignedInt(bytes: Int) = {
    require(bytes > 0 && bytes < 4)
    getUnsignedBigInt(bytes).toInt
  }

  def putUnsignedInt(bytes: Int, i: Int) = {
    require(bytes > 0 && bytes < 4)
    putUnsignedBigInt(bytes, i)
  }

  def getDomainName(): String = {
    def getDomainNamePart(positions: Set[Int]): List[String] = {
      getUnsignedInt(1) match {
        case s if (s & 0xC0) != 0 =>
          buf.position(buf.position() - 1)
          val ptr = getUnsignedInt(2) - 0xC000
          val pos = buf.position()
          assert(!(positions contains pos))
          buf.position(ptr)
          val dn = getDomainNamePart(positions + pos)
          buf.position(pos)
          dn
        case s if s == 0 => Nil
        case s => ((0 until s).map(_ => buf.get().toChar).mkString) :: getDomainNamePart(positions)
      }
    }
    getDomainNamePart(Set()).mkString(".")
  }

  def putDomainName(dn: String): MessageBuffer = {
    if (dn.isEmpty) {
      put(0: Byte)
    } else {
      domains.get(dn) match {
        case Some(pos) => putUnsignedInt(2, 0xC000 | pos)
        case None =>
          dn.span('.'!=) match {
            case (dc, d) =>
              val dr = if (d.isEmpty) d else d.substring(1)
              val pos = buf.position
              val mb = put(dc.size.toByte +: dc.getBytes).putDomainName(dr)
              new MessageBuffer(
                mb.buf,
                mb.domains + (dn -> pos))
          }
      }
    }
  }

  private def foreachBuf(f: (ByteBuffer) => Unit) = {
    f(buf)
    this
  }

  def putLengthOf(bytes: Int, f: (MessageBuffer) => MessageBuffer): MessageBuffer = {
    val oldPosition = buf.position()
    buf.position(oldPosition + bytes)
    val mb = f(this)
    val newPosition = mb.buf.position()
    mb
      .foreachBuf(_.position(oldPosition))
      .putUnsignedLong(bytes, newPosition - oldPosition - bytes)
      .foreachBuf(_.position(newPosition))
  }
}

object MessageBuffer {
  def apply() = new MessageBuffer(ByteBuffer.allocate(4096), Map())

  def apply(buf: ByteBuffer) = new MessageBuffer(buf, Map())
}
