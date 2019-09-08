Netty Codec
===========

The netty part contains the
[MessageToMessageCodec](http://netty.io/4.0/api/io/netty/handler/codec/MessageToMessageCodec.html)
[DnsCodec](https://github.com/mkroli/dns4s/blob/master/netty/src/main/scala/com/github/mkroli/dns4s/netty/DnsCodec.scala).

## Usage
If you're using [sbt] just add the following to your build definition:

@@@vars
```scala
resolvers += "bintray" at "http://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "com.github.mkroli" %% "dns4s-netty" % "$version$")
```
@@@

## Imports
Use the following additional imports to get started:
```scala mdoc:silent
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.netty._

import io.netty.util.concurrent.Future
```
```scala mdoc:invisible
import java.net.InetSocketAddress
import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramChannel
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.concurrent.GenericFutureListener
```

### Server
The following is an excerpt from [examples/simple-netty/../DnsServer.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple-netty/src/main/scala/com/github/mkroli/dns4s/examples/simple/netty/DnsServer.scala):
```scala mdoc:silent
class DnsServerHandler extends SimpleChannelInboundHandler[DnsPacket] {
  def channelRead0(ctx: ChannelHandlerContext, packet: DnsPacket): Unit = {
    Some(packet.content).collect {
      case Query(q) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
        Response(q) ~ Answers(RRName(host) ~ ARecord("1.2.3.4"))
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
      override def initChannel(ch: DatagramChannel): Unit = {
        ch.pipeline.addLast(new DnsCodec, new DnsServerHandler)
      }
    })
    .bind(5354)
}
```

### Client
The following is an excerpt from [examples/simple-netty-client/../DnsClient.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple-client/src/main/scala/com/github/mkroli/dns4s/examples/simple/client/DnsClient.scala):
```scala mdoc:silent
class DnsClientHandler(group: NioEventLoopGroup) extends SimpleChannelInboundHandler[DnsPacket] {
  def channelRead0(ctx: ChannelHandlerContext, packet: DnsPacket): Unit = {
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

val group = new NioEventLoopGroup
val channel: ChannelFuture = {
  new Bootstrap()
    .group(group)
    .channel(classOf[NioDatagramChannel])
    .handler(new ChannelInitializer[DatagramChannel] {
      override def initChannel(ch: DatagramChannel): Unit = {
        ch.pipeline.addLast(new DnsCodec, new DnsClientHandler(group))
      }
    })
    .bind(0)
    .addListener(new GenericFutureListener[Future[Void]] {
      override def operationComplete(f: Future[Void]): Unit = {
        channel.channel.writeAndFlush(DnsPacket(Query ~ Questions(QName("google.de")), new InetSocketAddress("8.8.8.8", 53)))
      }
    })
}
```

[sbt]:http://scala-sbt.org/
