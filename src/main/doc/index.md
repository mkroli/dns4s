@@@ index

* [Core](core.md)
* [Akka IO Extension](akka.md)
* [Cats Effect/FS2](fs2.md)
* [Netty Codec](netty.md)

@@@

dns4s
=====

[![Build Status](https://github.com/mkroli/dns4s/actions/workflows/scala_2.13.yml/badge.svg)](https://github.com/mkroli/dns4s/actions/workflows/scala_2.13.yml)
[![Coverage Status](http://coveralls.io/repos/mkroli/dns4s/badge.svg?branch=master&service=github)](http://coveralls.io/github/mkroli/dns4s?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.mkroli/dns4s-core_2.13)](https://search.maven.org/search?q=g:com.github.mkroli%20AND%20a:dns4s*)

dns4s is an implementation of the [DNS] protocol in [Scala].
It consists of the following components:

| Component                         |       Scala 2.10        |          2.11           |          2.12           |          2.13           |            3            |
|-----------------------------------|:-----------------------:|:-----------------------:|:-----------------------:|:-----------------------:|:-----------------------:|
| @ref:[Core](core.md)              | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} |
| @ref:[Akka IO Extension](akka.md) | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} |
| @ref:[Netty Codec](netty.md)      | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} |
| @ref:[Cats-Effect/FS2](fs2.md)    | @span[ ]{.md-icon .no}  | @span[ ]{.md-icon .no}  | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} | @span[ ]{.md-icon .yes} |

[Scala]:http://www.scala-lang.org
[DNS]:http://en.wikipedia.org/wiki/Domain_Name_System
