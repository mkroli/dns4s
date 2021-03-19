@@@ index

* [Dsl](dsl.md)

@@@

Core
====

The core part contains the functionality used for en-/decoding of DNS
messages as well as an inner-[DSL] to con-/destruct DNS messages.

For a detailed explanation of the DSL have a look at @ref:[its documentation](dsl.md).

For using it in a client/server application have a look at the @ref:[Akka IO Extension](akka.md) or the @ref:[Netty Codec](netty.md).

## Usage
If you're using [sbt] just add the following to your build definition:

@@@vars
```scala
libraryDependencies ++= Seq(
  "com.github.mkroli" %% "dns4s-core" % "$version$"
)
```
@@@

[DSL]:http://en.wikipedia.org/wiki/Domain-specific_language
