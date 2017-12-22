package com.github.mkroli.dns4s.dsl

import java.net.InetAddress

import com.github.mkroli.dns4s.section.resource.OPTResource
import com.github.mkroli.dns4s.section.resource.OPTResource.{ClientSubnetOPTOptionData, OPTOption, UnknownOPTOptionData}

private[dsl] abstract class OptionDataExtractor[T: Manifest] extends ContainedMatcher[OPTOption, T] {
  def unapply(o: OPTOption): Option[T] = o.data match {
    case o: T => Some(o)
    case _ => None
  }
}

object UnknownOption extends OptionDataExtractor[UnknownOPTOptionData] {
  def apply(code: Int, bytes: Array[Byte]): OPTOption =
    OPTOption(code, UnknownOPTOptionData(bytes))
}

object ClientSubnetOption extends OptionDataExtractor[ClientSubnetOPTOptionData] {
  def apply(family: Int, sourcePrefixLength: Int, scopePrefixLength: Int, address: InetAddress): OPTOption =
    OPTOption(OPTResource.optionClientSubnet, ClientSubnetOPTOptionData(family, sourcePrefixLength, scopePrefixLength, address))
}
