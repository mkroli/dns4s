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
package com.github.mkroli.dns4s.examples.simple.netty

import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.netty._

import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel

class DnsServerHandler extends SimpleChannelInboundHandler[DnsPacket] {
  def channelRead0(ctx: ChannelHandlerContext, packet: DnsPacket) {
    Some(packet.content).collect {
      case Query(_) ~ Id(id) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
        Response ~ Id(id) ~ Questions(host) ~ Answers(ARecord("1.2.3.4"))
    }.foreach { msg =>
      ctx.channel.writeAndFlush(DnsPacket(msg, packet.sender))
    }
  }
}

object DnsServer extends App {
  new Bootstrap()
    .group(new NioEventLoopGroup)
    .channel(classOf[NioDatagramChannel])
    .handler(new ChannelInitializer[DatagramChannel] {
      override def initChannel(ch: DatagramChannel) {
        ch.pipeline.addLast(new DnsCodec, new DnsServerHandler)
      }
    })
    .bind(5354)
}
