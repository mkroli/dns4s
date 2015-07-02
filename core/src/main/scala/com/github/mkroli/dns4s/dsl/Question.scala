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

trait QuestionSectionModifier { self =>
  def ~(qsm: QuestionSectionModifier): QuestionSectionModifier = new QuestionSectionModifier {
    override def apply(qs: QuestionSection): QuestionSection = qsm(self(qs))
  }

  def apply(qs: QuestionSection): QuestionSection
}

object Questions {
  def apply[T](question: T*)(implicit toQuestionSection: T => QuestionSection): MessageModifier = new MessageModifier {
    override def apply(msg: Message) = msg.copy(
      header = msg.header.copy(qdcount = msg.header.qdcount + question.size),
      question = msg.question ++ question.map(toQuestionSection))
  }

  def unapply(msg: Message): Option[Seq[QuestionSection]] = Some(msg.question.toList)
}

private[dsl] abstract class QuestionExtractor[T](e: QuestionSection => T) {
  def unapply(qs: QuestionSection): Option[T] = Some(e(qs))
}

object QName extends QuestionExtractor(_.qname) {
  def apply(qname: String): QuestionSectionModifier = new QuestionSectionModifier {
    override def apply(qs: QuestionSection) = qs.copy(qname = qname)
  }
}

object QType extends QuestionExtractor(_.qtype) {
  def apply(qtype: Int): QuestionSectionModifier = new QuestionSectionModifier {
    override def apply(qs: QuestionSection) = qs.copy(qtype = qtype)
  }
}

object QClass extends QuestionExtractor(_.qclass) {
  def apply(qclass: Int): QuestionSectionModifier = new QuestionSectionModifier {
    override def apply(qs: QuestionSection) = qs.copy(qclass = qclass)
  }
}
