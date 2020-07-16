Akka IO Extension
=================

The akka part contains an akka-io extension.

## Usage
If you're using [sbt] just add the following to your build definition:

@@@vars
```scala
resolvers += "bintray" at "http://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.12",
  "com.github.mkroli" %% "dns4s-akka" % "$version$")
```
@@@

### Imports
Use the following additional imports to get started:
```scala mdoc:silent
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.akka._
```
```scala mdoc:invisible
import scala.language.postfixOps

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
```

### Server
The following is an excerpt from [examples/simple/../DnsServer.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple/src/main/scala/com/github/mkroli/dns4s/examples/simple/DnsServer.scala):
```scala mdoc:silent
class DnsHandlerActor extends Actor {
  override def receive = {
    case Query(q) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
      sender() ! Response(q) ~ Answers(RRName(host) ~ ARecord("1.2.3.4"))
  }
}

object DnsServer extends App {
  implicit val system = ActorSystem("DnsServer")
  implicit val timeout = Timeout(5 seconds)
  IO(Dns) ? Dns.Bind(system.actorOf(Props[DnsHandlerActor]()), 5354)
}
```

### Client
The following is an excerpt from [examples/simple-client/../DnsClient.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple-client/src/main/scala/com/github/mkroli/dns4s/examples/simple/client/DnsClient.scala):
```scala mdoc:silent
object DnsClient extends App {
  implicit val system = ActorSystem("DnsServer")
  implicit val timeout = Timeout(5.seconds)
  import system.dispatcher

  IO(Dns) ? Dns.DnsPacket(Query ~ Questions(QName("google.de")), new java.net.InetSocketAddress("8.8.8.8", 53)) foreach {
    case Response(Answers(answers)) =>
      answers.collect {
        case ARecord(arecord) => println(arecord.address.getHostAddress)
      }
  }
}
```

[sbt]:http://scala-sbt.org/
