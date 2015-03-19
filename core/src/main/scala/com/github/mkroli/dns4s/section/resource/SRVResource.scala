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
