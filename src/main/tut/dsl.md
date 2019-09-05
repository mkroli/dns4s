DSL
===

Use the following imports for the examples:
```tut:invisible
import scala.language.postfixOps
```
```tut:silent
import com.github.mkroli.dns4s._
import com.github.mkroli.dns4s.dsl._
import com.github.mkroli.dns4s.section._
import com.github.mkroli.dns4s.section.resource._
```

Header
------

### ID
```tut:book
// Creation
val msg: Message = Query ~ Id(123)

// Matching
val id = msg match {
  case Id(id) => id
}
```

### QR
```tut:book
// Creation
val query: Message = Query
val response1: Message = Response
val response2: Message = Response(query)

// Matching
val qr = query match {
  case Query(msg)    => "query"
  case Response(msg) => "response"
}
```

### Opcode
```tut:book
// Creation
val query1: Message = Query ~ StandardQuery
val query2: Message = Query ~ InverseQuery
val query3: Message = Query ~ ServerStatusRequest

// Matching
val op = query1 match {
  case StandardQuery()       => "standard-query"
  case InverseQuery()        => "inverse-query"
  case ServerStatusRequest() => "server-status-request"
}
```

### AA
```tut:book
// Creation
val response: Message = Response ~ AuthoritativeAnswer

// Matching
val aa = response match {
  case AuthoritativeAnswer() => true
  case _ => false
}
```

### TC
```tut:book
// Creation
val response: Message = Response ~ Truncation

// Matching
val tc = response match {
  case Truncation() => true
  case _ => false
}
```

### RD
```tut:book
// Creation
val query: Message = Query ~ RecursionDesired

// Matching
val rd = query match {
  case RecursionDesired() => true
  case _ => false
}
```

### RA
```tut:book
// Creation
val response: Message = Response ~ RecursionAvailable

// Matching
val ra = response match {
  case RecursionAvailable() => true
  case _ => false
}
```

### RCODE
```tut:book
// Creation
val response1: Message = Response ~ NoError
val response2: Message = Response ~ FormatError
val response3: Message = Response ~ ServerFailure
val response4: Message = Response ~ NameError
val response5: Message = Response ~ NotImplemented
val response6: Message = Response ~ Refused

// Matching
val rCode = response1 match {
  case Response(r) ~ NoError()        => "no-error"
  case Response(r) ~ FormatError()    => "format-error"
  case Response(r) ~ ServerFailure()  => "server-failure"
  case Response(r) ~ NameError()      => "name-error"
  case Response(r) ~ NotImplemented() => "not-implemented"
  case Response(r) ~ Refused()        => "refused"
}
```

Sections
--------

### Question section
```tut:book
// Creation
val query: Message = Query ~ Questions()

// Matching
val questions = query match {
  case Query(_) ~ Questions(questions) => questions
}
```

#### QNAME
```tut:book
// Creation
val query1: Message = Query ~ Questions(QName("example.com"))
val query2: Message = Query ~ Questions(QName("example.com"), QName("www.example.com"))

// Matching
val names = query2 match {
  case Query(_) ~ Questions(QName(name1) :: QName(name2) :: Nil) => (name1, name2)
}
```

#### QTYPE
```tut:book
// Creation
val query: Message = Query ~ Questions(QName("example.com") ~ QType(ResourceRecord.typeTXT))

val queryA:        Message = Query ~ Questions(QName("example.com") ~ TypeA)
val queryNS:       Message = Query ~ Questions(QName("example.com") ~ TypeNS)
val queryMD:       Message = Query ~ Questions(QName("example.com") ~ TypeMD)
val queryMF:       Message = Query ~ Questions(QName("example.com") ~ TypeMF)
val queryCNAME:    Message = Query ~ Questions(QName("example.com") ~ TypeCNAME)
val querySOA:      Message = Query ~ Questions(QName("example.com") ~ TypeSOA)
val queryMB:       Message = Query ~ Questions(QName("example.com") ~ TypeMB)
val queryMG:       Message = Query ~ Questions(QName("example.com") ~ TypeMG)
val queryMR:       Message = Query ~ Questions(QName("example.com") ~ TypeMR)
val queryNULL:     Message = Query ~ Questions(QName("example.com") ~ TypeNULL)
val queryWKS:      Message = Query ~ Questions(QName("example.com") ~ TypeWKS)
val queryPTR:      Message = Query ~ Questions(QName("example.com") ~ TypePTR)
val queryHINFO:    Message = Query ~ Questions(QName("example.com") ~ TypeHINFO)
val queryMINFO:    Message = Query ~ Questions(QName("example.com") ~ TypeMINFO)
val queryMX:       Message = Query ~ Questions(QName("example.com") ~ TypeMX)
val queryTXT:      Message = Query ~ Questions(QName("example.com") ~ TypeTXT)
val queryAAAA:     Message = Query ~ Questions(QName("example.com") ~ TypeAAAA)
val querySRV:      Message = Query ~ Questions(QName("example.com") ~ TypeSRV)
val queryNAPTR:    Message = Query ~ Questions(QName("example.com") ~ TypeNAPTR)
val queryOPT:      Message = Query ~ Questions(QName("example.com") ~ TypeOPT)
val queryAXFR:     Message = Query ~ Questions(QName("example.com") ~ TypeAXFR)
val queryMAILB:    Message = Query ~ Questions(QName("example.com") ~ TypeMAILB)
val queryMAILA:    Message = Query ~ Questions(QName("example.com") ~ TypeMAILA)
val queryAsterisk: Message = Query ~ Questions(QName("example.com") ~ TypeAsterisk)

// Matching
val `type` = query match {
  case Query(q) ~ Questions(QName(name) ~ TypeA()        :: Nil) => "A"
  case Query(q) ~ Questions(QName(name) ~ TypeNS()       :: Nil) => "NS"
  case Query(q) ~ Questions(QName(name) ~ TypeMD()       :: Nil) => "MD"
  case Query(q) ~ Questions(QName(name) ~ TypeMF()       :: Nil) => "MF"
  case Query(q) ~ Questions(QName(name) ~ TypeCNAME()    :: Nil) => "CNAME"
  case Query(q) ~ Questions(QName(name) ~ TypeSOA()      :: Nil) => "SOA"
  case Query(q) ~ Questions(QName(name) ~ TypeMB()       :: Nil) => "MB"
  case Query(q) ~ Questions(QName(name) ~ TypeMG()       :: Nil) => "MG"
  case Query(q) ~ Questions(QName(name) ~ TypeMR()       :: Nil) => "MR"
  case Query(q) ~ Questions(QName(name) ~ TypeNULL()     :: Nil) => "NULL"
  case Query(q) ~ Questions(QName(name) ~ TypeWKS()      :: Nil) => "WKS"
  case Query(q) ~ Questions(QName(name) ~ TypePTR()      :: Nil) => "PTR"
  case Query(q) ~ Questions(QName(name) ~ TypeHINFO()    :: Nil) => "HINFO"
  case Query(q) ~ Questions(QName(name) ~ TypeMINFO()    :: Nil) => "MINFO"
  case Query(q) ~ Questions(QName(name) ~ TypeMX()       :: Nil) => "MX"
  case Query(q) ~ Questions(QName(name) ~ TypeTXT()      :: Nil) => "TXT"
  case Query(q) ~ Questions(QName(name) ~ TypeAAAA()     :: Nil) => "AAAA"
  case Query(q) ~ Questions(QName(name) ~ TypeSRV()      :: Nil) => "SRV"
  case Query(q) ~ Questions(QName(name) ~ TypeNAPTR()    :: Nil) => "NAPTR"
  case Query(q) ~ Questions(QName(name) ~ TypeOPT()      :: Nil) => "OPT"
  case Query(q) ~ Questions(QName(name) ~ TypeAXFR()     :: Nil) => "AXFR"
  case Query(q) ~ Questions(QName(name) ~ TypeMAILB()    :: Nil) => "MAILB"
  case Query(q) ~ Questions(QName(name) ~ TypeMAILA()    :: Nil) => "MAILA"
  case Query(q) ~ Questions(QName(name) ~ TypeAsterisk() :: Nil) => "Asterisk"

  case Query(q) ~ Questions(QName(name) ~ QType(t) :: Nil) => t.toString
}
```

#### QCLASS
```tut:book
// Creation
val query: Message = Query ~ Questions(QName("example.com") ~ QType(ResourceRecord.typeA) ~ QClass(ResourceRecord.classIN))

val queryIN:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassIN)
val queryCS:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassCS)
val queryCH:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassCH)
val queryHS:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassHS)
val queryAsterisk: Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassAsterisk)

// Matching
val qclass = query match {
  case Query(q) ~ Questions(QName(name) ~ QType(t) ~ QClass(c) :: Nil) => c

  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassIN() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassCS() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassCH() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassHS() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassAsterisk() :: Nil) => ???
}
```

### Answer section
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers()

// Matching
val answers = response match {
  case Response(r) ~ Answers(answers) => answers
}
```

### Authority records section
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Authority()

// Matching
val authority = response match {
  case Response(r) ~ Authority(authority) => authority
}
```

### Additional records section
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Additional()

// Matching
val additional = response match {
  case Response(r) ~ Additional(additional) => additional
}
```

### Resource record

#### NAME
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(RRName("example.com"))

// Matching
val name = response match {
  case Response(r) ~ Answers(RRName(name) :: Nil) => name
}
```

#### TYPE
```tut:book
// Creation
val response:         Message = Response ~ Answers(RRName("example.com") ~ RRType(ResourceRecord.typeTXT))

val responseA:        Message = Response ~ Answers(RRName("example.com") ~ TypeA)
val responseNS:       Message = Response ~ Answers(RRName("example.com") ~ TypeNS)
val responseMD:       Message = Response ~ Answers(RRName("example.com") ~ TypeMD)
val responseMF:       Message = Response ~ Answers(RRName("example.com") ~ TypeMF)
val responseCNAME:    Message = Response ~ Answers(RRName("example.com") ~ TypeCNAME)
val responseSOA:      Message = Response ~ Answers(RRName("example.com") ~ TypeSOA)
val responseMB:       Message = Response ~ Answers(RRName("example.com") ~ TypeMB)
val responseMG:       Message = Response ~ Answers(RRName("example.com") ~ TypeMG)
val responseMR:       Message = Response ~ Answers(RRName("example.com") ~ TypeMR)
val responseNULL:     Message = Response ~ Answers(RRName("example.com") ~ TypeNULL)
val responseWKS:      Message = Response ~ Answers(RRName("example.com") ~ TypeWKS)
val responsePTR:      Message = Response ~ Answers(RRName("example.com") ~ TypePTR)
val responseHINFO:    Message = Response ~ Answers(RRName("example.com") ~ TypeHINFO)
val responseMINFO:    Message = Response ~ Answers(RRName("example.com") ~ TypeMINFO)
val responseMX:       Message = Response ~ Answers(RRName("example.com") ~ TypeMX)
val responseTXT:      Message = Response ~ Answers(RRName("example.com") ~ TypeTXT)
val responseAAAA:     Message = Response ~ Answers(RRName("example.com") ~ TypeAAAA)
val responseSRV:      Message = Response ~ Answers(RRName("example.com") ~ TypeSRV)
val responseNAPTR:    Message = Response ~ Answers(RRName("example.com") ~ TypeNAPTR)
val responseOPT:      Message = Response ~ Answers(RRName("example.com") ~ TypeOPT)
val responseAXFR:     Message = Response ~ Answers(RRName("example.com") ~ TypeAXFR)
val responseMAILB:    Message = Response ~ Answers(RRName("example.com") ~ TypeMAILB)
val responseMAILA:    Message = Response ~ Answers(RRName("example.com") ~ TypeMAILA)
val responseCAA:      Message = Response ~ Answers(RRName("example.com") ~ TypeCAA)
val responseAsterisk: Message = Response ~ Answers(RRName("example.com") ~ TypeAsterisk)

// Matching
val rtype = response match {
  case Response(r) ~ Answers(RRName(name) ~ TypeA()        :: Nil) => "A"
  case Response(r) ~ Answers(RRName(name) ~ TypeNS()       :: Nil) => "NS"
  case Response(r) ~ Answers(RRName(name) ~ TypeMD()       :: Nil) => "MD"
  case Response(r) ~ Answers(RRName(name) ~ TypeMF()       :: Nil) => "MF"
  case Response(r) ~ Answers(RRName(name) ~ TypeCNAME()    :: Nil) => "CNAME"
  case Response(r) ~ Answers(RRName(name) ~ TypeSOA()      :: Nil) => "SOA"
  case Response(r) ~ Answers(RRName(name) ~ TypeMB()       :: Nil) => "MB"
  case Response(r) ~ Answers(RRName(name) ~ TypeMG()       :: Nil) => "MG"
  case Response(r) ~ Answers(RRName(name) ~ TypeMR()       :: Nil) => "MR"
  case Response(r) ~ Answers(RRName(name) ~ TypeNULL()     :: Nil) => "NULL"
  case Response(r) ~ Answers(RRName(name) ~ TypeWKS()      :: Nil) => "WKS"
  case Response(r) ~ Answers(RRName(name) ~ TypePTR()      :: Nil) => "PTR"
  case Response(r) ~ Answers(RRName(name) ~ TypeHINFO()    :: Nil) => "HINFO"
  case Response(r) ~ Answers(RRName(name) ~ TypeMINFO()    :: Nil) => "MINFO"
  case Response(r) ~ Answers(RRName(name) ~ TypeMX()       :: Nil) => "MX"
  case Response(r) ~ Answers(RRName(name) ~ TypeTXT()      :: Nil) => "TXT"
  case Response(r) ~ Answers(RRName(name) ~ TypeAAAA()     :: Nil) => "AAAA"
  case Response(r) ~ Answers(RRName(name) ~ TypeSRV()      :: Nil) => "SRV"
  case Response(r) ~ Answers(RRName(name) ~ TypeNAPTR()    :: Nil) => "NAPTR"
  case Response(r) ~ Answers(RRName(name) ~ TypeOPT()      :: Nil) => "OPT"
  case Response(r) ~ Answers(RRName(name) ~ TypeAXFR()     :: Nil) => "AXFR"
  case Response(r) ~ Answers(RRName(name) ~ TypeMAILB()    :: Nil) => "MAILB"
  case Response(r) ~ Answers(RRName(name) ~ TypeMAILA()    :: Nil) => "MAILA"
  case Response(r) ~ Answers(RRName(name) ~ TypeCAA()      :: Nil) => "CAA"
  case Response(r) ~ Answers(RRName(name) ~ TypeAsterisk() :: Nil) => "Asterisk"

  case Response(r) ~ Answers(RRName(name) ~ RRType(t)      :: Nil) => t.toString
}
```

#### CLASS
```tut:book
// Creation
val response:         Message = Response ~ Answers(RRName("example.com") ~ RRClass(ResourceRecord.classIN))

val responseIN:       Message = Response ~ Answers(RRName("example.com") ~ ClassIN)
val responseCS:       Message = Response ~ Answers(RRName("example.com") ~ ClassCS)
val responseCH:       Message = Response ~ Answers(RRName("example.com") ~ ClassCH)
val responseHS:       Message = Response ~ Answers(RRName("example.com") ~ ClassHS)
val responseAsterisk: Message = Response ~ Answers(RRName("example.com") ~ ClassAsterisk)

// Matching
val `class` = response match {
  case Response(q) ~ Answers(ClassIN()       :: Nil) => "IN"
  case Response(q) ~ Answers(ClassCS()       :: Nil) => "CS"
  case Response(q) ~ Answers(ClassCH()       :: Nil) => "CH"
  case Response(q) ~ Answers(ClassHS()       :: Nil) => "HS"
  case Response(q) ~ Answers(ClassAsterisk() :: Nil) => "Asterisk"

  case Response(q) ~ Answers(RRClass(c)       :: Nil) => c.toString
}
```

#### TTL
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(RRName("example.com") ~ RRType(ResourceRecord.typeTXT) ~ RRClass(ResourceRecord.classIN) ~ RRTtl(123))

// Matching
val ttl = response match {
  case Response(r) ~ Answers(RRName(name) ~ RRType(t) ~ RRClass(c) ~ RRTtl(ttl) :: Nil) => ttl
}
```

### Resource records

#### ARecord
```tut:book
// Creation
val address: java.net.Inet4Address = java.net.InetAddress.getByAddress(Array.fill[Byte](4)(0)).asInstanceOf[java.net.Inet4Address]
val response1: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord(address))
val response2: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord(Array[Byte](1, 2, 3, 4)))
val response3: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord("1.2.3.4"))

// Matching
val address = response1 match {
  case Response(_) ~ Answers(ARecord(r) :: Nil) => r.address
}
```

#### AAAARecord
```tut:book
// Creation
val address: java.net.Inet6Address = java.net.InetAddress.getByAddress(Array.fill[Byte](16)(0)).asInstanceOf[java.net.Inet6Address]
val response1: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord(address))
val response2: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord(Array[Byte](1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)))
val response3: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord("0123:4567:89ab:cdef:0123:4567:89ab:cdef"))

// Matching
val address = response1 match {
  case Response(_) ~ Answers(AAAARecord(r) :: Nil) => r.address
}
```

#### CNameRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("www.example.com") ~ TypeCNAME) ~ Answers(CNameRecord("example.com"))

// Matching
val cname = response match {
  case Response(_) ~ Answers(CNameRecord(r) :: Nil) => r.cname
}
```

#### MXRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeMX) ~ Answers(MXRecord(1, "example.com"))

// Matching
val (preference, exchange) = response match {
  case Response(_) ~ Answers(MXRecord(r) :: Nil) => (r.preference, r.exchange)
}
```

#### NAPTRRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeNAPTR) ~ Answers(NAPTRRecord(1, 1, "A", "", "!^.*$!example.com!", ""))

// Matching
val (order, preference, flags, services, regexp, replacement) = response match {
  case Response(_) ~ Answers(NAPTRRecord(r) :: Nil) => (r.order, r.preference, r.flags, r.services, r.regexp, r.replacement)
}
```

#### OPTRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeOPT) ~ Additional(OPTRecord(Nil))

// Matching
val optRecord = response match {
  case Response(_) ~ Additional(OPTRecord(r) :: Nil) => r
}
```

##### ClientSubnetOption
```tut:book
// Creation
val response: Message = Response ~ Additional(OPTRecord(ClientSubnetOption(OPTResource.ClientSubnetOPTOptionData.familyIPv4, 24, 0, java.net.InetAddress.getByName("1.2.3.0")) :: Nil))

// Matching
val (family, sourcePrefixLength, scopePrefixLength, address) = response match {
  case Response(_) ~ Additional(OPTRecord(OPTResource(ClientSubnetOption(OPTResource.ClientSubnetOPTOptionData(family, sourcePrefixLength, scopePrefixLength, address)) :: Nil)) :: Nil) => (family, sourcePrefixLength, scopePrefixLength, address)
}
```

#### NSRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeNS) ~ Answers(NSRecord("example.com"))

// Matching
val nsdname = response match {
  case Response(_) ~ Answers(NSRecord(r) :: Nil) => r.nsdname
}
```

#### PTRRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypePTR) ~ Answers(PTRRecord("example.com"))

// Matching
val ptrdname = response match {
  case Response(_) ~ Answers(PTRRecord(r) :: Nil) => r.ptrdname
}
```

#### HInfoRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeHINFO) ~ Answers(HInfoRecord("CPU", "Linux"))

// Matching
val (cpu, os) = response match {
  case Response(_) ~ Answers(HInfoRecord(r) :: Nil) => (r.cpu, r.os)
}
```

#### TXTRecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeTXT) ~ Answers(TXTRecord("Test", "test", "tesT"))

// Matching
val txt = response match {
  case Response(_) ~ Answers(TXTRecord(r) :: Nil) => r.txt
}
```

#### SOARecord
```tut:book
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeSOA) ~ Answers(SOARecord("example.com", "admin.example.com", 2015122401L, 3600L, 1800L, 604800L, 600L))

// Matching
val (mname, rname, serial, refresh, retry, expire, minimum) = response match {
  case Response(_) ~ Answers(SOARecord(r) :: Nil) => (r.mname, r.rname, r.serial, r.refresh, r.retry, r.expire, r.minimum)
}
```

#### CAARecord
```tut:book
// Creation
val response: Message = Response ~
                        Questions(QName("example.com") ~ TypeCAA) ~ 
                        Answers(RRName("example.com") ~ CAARecord.Issue("cert-authority.org"))

// Matching
val value = response match {
  case Response(_) ~ Answers(CAARecord.Issue(r) :: Nil) => r.value
}
```

Misc
----

### DnsClassName
```tut:book
// Matching
val msg: Message = Response ~ Questions(QName("example.com") ~ TypeTXT) ~ Answers(TXTRecord("Test", "test", "tesT"))
val (qclass, aclass) = msg match {
  case Response(_) ~ Questions(DnsClassName(qclass) :: Nil) ~ Answers(DnsClassName(aclass) :: Nil) => (qclass, aclass)
}
```

### DnsTypeName
The DnsTypeName object can be used to extract a String representation of a Question or a ResourceRecord.
```tut:book
// Matching
val msg: Message = Response ~ Questions(QName("example.com") ~ TypeTXT) ~ Answers(TXTRecord("Test", "test", "tesT"))
val (qtype, atype) = msg match {
  case Response(_) ~ Questions(DnsTypeName(qtype) :: Nil) ~ Answers(DnsTypeName(atype) :: Nil) => (qtype, atype)
}
```

### EDNS
```tut:book
// Creation
val query1: Message = Query ~ Questions(QName("example.com") ~ TypeA) ~ EDNS()
val query2: Message = Query ~ Questions(QName("example.com") ~ TypeA) ~ EDNS(4096)

// Matching
val size = query1 match {
  case Query(_) ~ Questions(q) ~ EDNS(size) => size
}
```
