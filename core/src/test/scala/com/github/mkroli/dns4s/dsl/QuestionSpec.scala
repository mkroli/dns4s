/*
 * Copyright 2015 Michael Krolikowski
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

import org.scalatest.FunSpec

import com.github.mkroli.dns4s.section.ResourceRecord

class QuestionSpec extends FunSpec {
  describe("QuestionSection") {
    it("should be possible to create no questions") {
      Query ~ Questions() match {
        case Query(msg ~ Questions(Nil)) =>
          assert(msg.header.qdcount === 0)
          assert(msg.question.isEmpty)
      }
    }

    it("should be possible to create one question") {
      Response ~ Questions(QName("test")) match {
        case Response(msg ~ Questions(QName("test") :: Nil)) =>
          assert(msg.header.qdcount === 1)
          val (question :: Nil) = msg.question
          assert(question.qname === "test")
      }
    }

    it("should be possible to create multiple questions") {
      Query ~ Questions(QName("test1"), QName("test2")) match {
        case Query(msg ~ Questions(QName("test1") :: QName("test2") :: Nil)) =>
          assert(msg.header.qdcount === 2)
          val (question1 :: question2 :: Nil) = msg.question
          assert(question1.qname === "test1")
          assert(question2.qname === "test2")
      }
    }

    it("should be possible to set the class") {
      Response ~ Questions(QName("test") ~ ClassCH) match {
        case Response(msg ~ Questions(QName("test") ~ ClassCH() :: Nil)) =>
          assert(msg.question.head.qclass === ResourceRecord.classCH)
      }

      Response ~ Questions(QName("test") ~ QClass(ResourceRecord.classCS)) match {
        case Response(msg ~ Questions(QName("test") ~ QClass(ResourceRecord.classCS) :: Nil)) =>
          assert(msg.question.head.qclass === ResourceRecord.classCS)
      }
    }

    it("should be possible to set the type") {
      Query ~ Questions(QName("test") ~ TypeTXT) match {
        case Query(msg ~ Questions(QName("test") ~ TypeTXT() :: Nil)) =>
          assert(msg.question.head.qtype === ResourceRecord.typeTXT)
      }

      Query ~ Questions(QName("test") ~ QType(ResourceRecord.typeHINFO)) match {
        case Query(msg ~ Questions(QName("test") ~ QType(ResourceRecord.typeHINFO) :: Nil)) =>
          assert(msg.question.head.qtype === ResourceRecord.typeHINFO)
      }
    }

    it("should be possible to set the class and the type") {
      Response ~ Questions(QName("test") ~ ClassCH ~ QType(ResourceRecord.typeTXT)) match {
        case Response(msg ~ Questions(QName("test") ~ QClass(ResourceRecord.classCH) ~ TypeTXT() :: Nil)) =>
          assert(msg.question.head.qclass === ResourceRecord.classCH)
      }

      Response ~ Questions(QName("test") ~ QClass(ResourceRecord.classCS) ~ TypeHINFO) match {
        case Response(msg ~ Questions(QName("test") ~ ClassCS() ~ QType(ResourceRecord.typeHINFO) :: Nil)) =>
          assert(msg.question.head.qclass === ResourceRecord.classCS)
      }
    }
  }
}
