/*
 * Copyright 2015-2019 Michael Krolikowski
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
package com.github.mkroli.dns4s.akka

import java.net.{InetAddress, InetSocketAddress}
import akka.actor._
import akka.io.IO
import akka.testkit.{ImplicitSender, TestKitBase, TestProbe}
import akka.util.Timeout
import com.github.mkroli.dns4s.DnsTestUtils
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.section.resource.AResource
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec

import scala.concurrent.duration.DurationInt
import scala.language.{implicitConversions, postfixOps}

trait DefaultActorSystem {
  self: TestKitBase =>

  override implicit val system: ActorSystem = ActorSystem()
}

class DnsExtensionSpec extends AnyFunSpec with DefaultActorSystem with TestKitBase with ImplicitSender with BeforeAndAfterAll with DnsTestUtils {
  implicit val timeout: Timeout = Timeout(5 seconds)

  override def afterAll() = shutdown(system)

  describe("DnsExtension") {
    describe("Binding/Unbinding") {
      it("should be possible to bind to an available port") {
        IO(Dns) ! Dns.Bind(testActor, nextAvailablePort())
        expectMsg(Dns.Bound)
        lastSender ! Dns.Unbind
        expectMsg(Dns.Unbound)
        expectNoMsg()
      }

      it("should not be possible to bind to an invalid port") {
        IO(Dns) ! Dns.Bind(testActor, 1)
        expectMsg(Dns.Unbound)
        expectNoMsg()
      }
    }

    describe("Sending/Receiving messages") {
      def withServer(test: (Int, ActorRef) => Unit) = {
        val port = nextAvailablePort()
        IO(Dns) ! Dns.Bind(testActor, port)
        expectMsg(Dns.Bound)
        val s = lastSender

        (1 to 100).foreach { _ =>
          test(port, s)
        }

        s ! Dns.Unbind
        expectMsg(Dns.Unbound)
        expectNoMsg()
      }

      def test(serverPort: Int, dnsActor: ActorRef): Unit = {
        val probe = TestProbe()
        dnsActor.tell(Dns.DnsPacket(Query ~ Questions(QName("test.test")), new InetSocketAddress(InetAddress.getLocalHost, serverPort)), probe.ref)
        val (id, questions) = expectMsgPF() {
          case Query(_) ~ Id(id) ~ Questions(q @ (QName("test.test") :: Nil)) => (id, q)
        }
        lastSender ! Response ~ Id(id) ~ Questions(questions: _*) ~ Answers(RRName("test.test") ~ ARecord("1.2.3.4"))
        probe.expectMsgPF() {
          case Response(_) ~ Id(`id`) ~ Questions(`questions`) ~ Answers(RRName("test.test") ~ ARecord(AResource(ip)) :: Nil)
              if ip.getHostAddress == "1.2.3.4" =>
        }
      }

      it("should be possible to send and receive queries and responses using a bound DnsActor") {
        withServer { (port, server) =>
          test(port, server)
        }
      }

      it("should be possible to send and receive queries and responses using IO(Dns) directly") {
        withServer { (port, _) =>
          test(port, IO(Dns))
        }
      }
    }
  }
}
