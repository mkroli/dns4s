Cats Effect/FS2
===============

The fs2 part contains functions using [FS2] to create [Cats]-[Effects](https://typelevel.org/cats-effect/docs/concepts#effects) / [Resources](https://typelevel.org/cats-effect/docs/std/resource).

## Usage
If you're using [sbt] just add the following to your build definition:

@@@vars
```scala
libraryDependencies ++= Seq(
  "co.fs2"            %% "fs2-core"  % "3.2.4",
  "com.github.mkroli" %% "dns4s-fs2" % "$version$"
)
```
@@@

### Imports
Use the following additional imports to get started:
```scala mdoc:silent
import cats.effect._
import com.comcast.ip4s._

import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.fs2.Dns
```

### Server
The following is an excerpt from [examples/simple-fs2/../DnsServer.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple-fs2/src/main/scala/com/github/mkroli/dns4s/examples/simple/fs2/DnsServer.scala):
```scala mdoc:silent
object DnsServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Dns
      .server[IO](port = Some(port"5354")) {
        case Query(q) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
          IO.pure(Response(q) ~ Answers(RRName(host) ~ ARecord("1.2.3.4")))
      }
      .as(ExitCode.Success)
  }
}
```

### Client
The following is an excerpt from [examples/simple-fs2-client/../DnsClient.scala](https://github.com/mkroli/dns4s/blob/master/examples/simple-fs2-client/src/main/scala/com/github/mkroli/dns4s/examples/simple/fs2/client/DnsClient.scala):
```scala mdoc:silent
object DnsClient extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    Dns
      .client[IO](SocketAddress(ip"8.8.8.8", port"53"))
      .use { dns =>
        for {
          address <- dns.queryFor(Query ~ Questions(QName("google.com"))) {
            case Response(Answers(answers)) => IO.fromOption {
              answers.collectFirst {
                case ARecord(arecord) => arecord.address.getHostAddress
              }
            }(new RuntimeException("Response doesn't contain A-Record"))
          }
          _ <- IO.println(address)
        } yield ExitCode.Success
      }
  }
}
```

[sbt]:http://scala-sbt.org/
[FS2]:https://fs2.io/
[Cats]:https://typelevel.org/cats-effect/
