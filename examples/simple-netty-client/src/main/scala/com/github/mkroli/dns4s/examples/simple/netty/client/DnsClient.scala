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
package com.github.mkroli.dns4s.examples.simple.netty.client

import java.net.InetSocketAddress

import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.netty._

import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.concurrent.{ Future => NettyFuture }
import io.netty.util.concurrent.GenericFutureListener

class DnsClientHandler(group: NioEventLoopGroup) extends SimpleChannelInboundHandler[DnsPacket] {
  def channelRead0(ctx: ChannelHandlerContext, packet: DnsPacket) {
    packet.content match {
      case Response(Answers(answers)) =>
        answers.collect {
          case ARecord(arecord) => println(arecord.address.getHostAddress)
        }
      case _ =>
    }
    group.shutdownGracefully()
  }
}

object DnsClient extends App {
  val group = new NioEventLoopGroup
  val channel: ChannelFuture = new Bootstrap()
    .group(group)
    .channel(classOf[NioDatagramChannel])
    .handler(new ChannelInitializer[DatagramChannel] {
      override def initChannel(ch: DatagramChannel) {
        ch.pipeline.addLast(new DnsCodec, new DnsClientHandler(group))
      }
    })
    .bind(0)
    .addListener(new GenericFutureListener[NettyFuture[Void]] {
      override def operationComplete(f: NettyFuture[Void]) {
        channel.channel.writeAndFlush(DnsPacket(Query ~ Questions(QName("google.de")), new InetSocketAddress("8.8.8.8", 53)))
      }
    })
}
