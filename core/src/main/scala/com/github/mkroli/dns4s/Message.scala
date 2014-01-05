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

import com.github.mkroli.dns4s.section.HeaderSection
import com.github.mkroli.dns4s.section.QuestionSection
import com.github.mkroli.dns4s.section.ResourceRecord

case class Message(
  header: HeaderSection,
  question: Seq[QuestionSection],
  answer: Seq[ResourceRecord],
  authority: Seq[ResourceRecord],
  additional: Seq[ResourceRecord]) {
  def apply(): MessageBuffer = {
    (header +:
      (question ++
        answer ++
        authority ++
        additional)).foldLeft(MessageBuffer()) { (buf, encoder) =>
          encoder(buf)
        }
  }
}

object Message {
  def apply(bytes: MessageBuffer): Message = {
    val header = HeaderSection(bytes)
    new Message(
      header,
      (1 to header.qdcount).map(_ => QuestionSection(bytes)),
      (1 to header.ancount).map(_ => ResourceRecord(bytes)),
      (1 to header.nscount).map(_ => ResourceRecord(bytes)),
      (1 to header.arcount).map(_ => ResourceRecord(bytes)))
  }
}
