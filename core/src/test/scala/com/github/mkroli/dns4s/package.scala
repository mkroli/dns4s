/*
 * Copyright 2014 Michael Krolikowski
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
package com.github.mkroli

import scala.language.postfixOps

import org.scalacheck.Gen

package object dns4s {
  def maxLong(bits: Int) = BigInt(2).pow(bits) - 1 toLong

  def maxInt(bits: Int) = maxLong(bits).toInt

  private val hexChars = ('0' to '9') ++ ('A' to 'Z') ++ ('a' to 'z') toSet

  def bytes(s: String) = s.filter(hexChars).sliding(2, 2).map(BigInt(_, 16).toByte).toList

  def bytesGenerator(min: Int = 0, max: Int = 4096) = Gen.choose(min, max).flatMap { size =>
    val byteGenerator = Gen.choose(Byte.MinValue, Byte.MaxValue)
    Gen.listOfN(size, byteGenerator)
  }.map(_.toArray)
}
