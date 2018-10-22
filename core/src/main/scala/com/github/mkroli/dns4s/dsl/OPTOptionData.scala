/*
 * Copyright 2017 Michael Krolikowski
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

import java.net.InetAddress

import com.github.mkroli.dns4s.section.resource.OPTResource
import com.github.mkroli.dns4s.section.resource.OPTResource.{ClientSubnetOPTOptionData, OPTOption, UnknownOPTOptionData}

private[dsl] abstract class OptionDataExtractor[T: Manifest] {
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
