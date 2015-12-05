DSL
===

Header
------

### ID
```scala
// Creation
val msg: Message = Query ~ Id(123)

// Matching
msg match {
  case Id(id) => id
}
```

### QR
```scala
// Creation
val query: Message = Query
val response1: Message = Response
val response2: Message = Response(query)

// Matching
query match {
  case Query(msg)    => msg
  case Response(msg) => msg
}
```

### Opcode
```scala
// Creation
val query1: Message = Query ~ StandardQuery
val query2: Message = Query ~ InverseQuery
val query3: Message = Query ~ ServerStatusRequest

// Matching
query1 match {
  case StandardQuery()       => ???
  case InverseQuery()        => ???
  case ServerStatusRequest() => ???
}
```

### AA
```scala
// Creation
val response: Message = Response ~ AuthoritativeAnswer

// Matching
response match {
  case AuthoritativeAnswer() => ???
}
```

### TC
```scala
// Creation
val response: Message = Response ~ Truncation

// Matching
response match {
  case Truncation() => ???
}
```

### RD
```scala
// Creation
val query: Message = Query ~ RecursionDesired

// Matching
query match {
  case RecursionDesired() => ???
}
```

### RA
```scala
// Creation
val response: Message = Response ~ RecursionAvailable

// Matching
response match {
  case RecursionAvailable() => ???
}
```

### RCODE
```scala
// Creation
val response1: Message = Response ~ NoError
val response2: Message = Response ~ FormatError
val response3: Message = Response ~ ServerFailure
val response4: Message = Response ~ NameError
val response5: Message = Response ~ NotImplemented
val response6: Message = Response ~ Refused

// Matching
response1 match {
  case Response(r) ~ NoError()        => ???
  case Response(r) ~ FormatError()    => ???
  case Response(r) ~ ServerFailure()  => ???
  case Response(r) ~ NameError()      => ???
  case Response(r) ~ NotImplemented() => ???
  case Response(r) ~ Refused()        => ???
}
```

Sections
--------

### Question section
```scala
// Creation
val query: Message = Query ~ Questions()

// Matching
query match {
  case Query(q) ~ Questions(Nil) => ???
}
```

#### QNAME
```scala
// Creation
val query1: Message = Query ~ Questions(QName("example.com"))
val query2: Message = Query ~ Questions(QName("example.com") ~ QName("www.example.com"))

// Matching
query2 match {
  case Query(q) ~ Questions(QName(name1) :: QName(name2) :: Nil) => (name1, name2)
}
```

#### QTYPE
```scala
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
query match {
  case Query(q) ~ Questions(QName(name) ~ TypeA()        :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeNS()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMD()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMF()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeCNAME()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeSOA()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMB()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMG()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMR()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeNULL()     :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeWKS()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypePTR()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeHINFO()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMINFO()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMX()       :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeTXT()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeAAAA()     :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeSRV()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeNAPTR()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeOPT()      :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeAXFR()     :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMAILB()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeMAILA()    :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeAsterisk() :: Nil) => ???

  case Query(q) ~ Questions(QName(name) ~ QType(t) :: Nil) => t
}
```

#### QCLASS
```scala
// Creation
val query: Message = Query ~ Questions(QName("example.com") ~ QType(ResourceRecord.typeA) ~ QClass(ResourceRecord.classIN))

val queryIN:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassIN)
val queryCS:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassCS)
val queryCH:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassCH)
val queryHS:       Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassHS)
val queryAsterisk: Message = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassAsterisk)

// Matching
query match {
  case Query(q) ~ Questions(QName(name) ~ QType(t) ~ QClass(c) :: Nil) => c

  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassIN() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassCS() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassCH() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassHS() :: Nil) => ???
  case Query(q) ~ Questions(QName(name) ~ TypeA() ~ ClassAsterisk() :: Nil) => ???
}
```

### Answer section
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers()

// Matching
response match {
  case Response(r) ~ Answers(Nil) => ???
}
```

### Authority records section
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Authority()

// Matching
response match {
  case Response(r) ~ Authority(Nil) => ???
}
```

### Additional records section
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Additional()

// Matching
response match {
  case Response(r) ~ Additional(Nil) => ???
}
```

### Resource record

#### NAME
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(RRName("example.com"))

// Matching
response match {
  case Response(r) ~ Answers(RRName(name) :: Nil) => name
}
```

#### TYPE
```scala
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
val responseAsterisk: Message = Response ~ Answers(RRName("example.com") ~ TypeAsterisk)

// Matching
response match {
  case Response(r) ~ Answers(RRName(name) ~ TypeA()        :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeNS()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMD()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMF()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeCNAME()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeSOA()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMB()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMG()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMR()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeNULL()     :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeWKS()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypePTR()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeHINFO()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMINFO()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMX()       :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeTXT()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeAAAA()     :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeSRV()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeNAPTR()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeOPT()      :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeAXFR()     :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMAILB()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeMAILA()    :: Nil) => ???
  case Response(r) ~ Answers(RRName(name) ~ TypeAsterisk() :: Nil) => ???

  case Response(r) ~ Answers(RRName(name) ~ RRType(t)      :: Nil) => t
}
```

#### CLASS
```scala
// Creation
val response:         Message = Response ~ Answers(RRName("example.com") ~ RRClass(ResourceRecord.classIN))

val responseIN:       Message = Response ~ Answers(RRName("example.com") ~ ClassIN)
val responseCS:       Message = Response ~ Answers(RRName("example.com") ~ ClassCS)
val responseCH:       Message = Response ~ Answers(RRName("example.com") ~ ClassCH)
val responseHS:       Message = Response ~ Answers(RRName("example.com") ~ ClassHS)
val responseAsterisk: Message = Response ~ Answers(RRName("example.com") ~ ClassAsterisk)

// Matching
response match {
  case Response(q) ~ Answers(ClassIN()       :: Nil) => ???
  case Response(q) ~ Answers(ClassCS()       :: Nil) => ???
  case Response(q) ~ Answers(ClassCH()       :: Nil) => ???
  case Response(q) ~ Answers(ClassHS()       :: Nil) => ???
  case Response(q) ~ Answers(ClassAsterisk() :: Nil) => ???

  case Response(q) ~ Answers(QClass(c)       :: Nil) => c
}
```

#### TTL
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(RRName("example.com") ~ RRType(ResourceRecord.typeTXT) ~ RRClass(ResourceRecord.classIN) ~ RRTtl(123))

// Matching
response match {
  case Response(r) ~ Answers(RRName(name) ~ RRType(t) ~ RRClass(c) ~ RRTtl(ttl) :: Nil) => ttl
}
```

### Resource records

#### ARecord
```scala
// Creation
val address: Inet4Address = ???
val response1: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord(address))
val response2: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord(Array[Byte](1, 2, 3, 4)))
val response3: Message = Response ~ Questions(QName("example.com") ~ TypeA) ~ Answers(ARecord("1.2.3.4"))

// Matching
response1 match {
  case Response(_) ~ Answers(ARecord(r) :: Nil) => r.address
}
```

#### AAAARecord
```scala
// Creation
val address: Inet6Address = ???
val response1: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord(address))
val response2: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord(Array[Byte](1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)))
val response3: Message = Response ~ Questions(QName("example.com") ~ TypeAAAA) ~ Answers(AAAARecord("0123:4567:89ab:cdef:0123:4567:89ab:cdef"))

// Matching
response1 match {
  case Response(_) ~ Answers(AAAARecord(r) :: Nil) => r.address
}
```

#### CNameRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("www.example.com") ~ TypeCNAME) ~ Answers(CNameRecord("example.com"))

// Matching
response match {
  case Response(_) ~ Answers(CNameRecord(r) :: Nil) => r.cname
}
```

#### MXRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeMX) ~ Answers(MXRecord(1, "example.com"))

// Matching
response match {
  case Response(_) ~ Answers(MXRecord(r) :: Nil) => (r.preference, r.exchange)
}
```

#### NAPTRRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeNAPTR) ~ Answers(NAPTRRecord(1, 1, "A", "", "!^.*$!example.com!", ""))

// Matching
response match {
  case Response(_) ~ Answers(NAPTRRecord(r) :: Nil) => (r.order, r.preference, r.flags, r.services, r.regexp, r.replacement)
}
```

#### OPTRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeOPT) ~ Answers(OPTRecord())

// Matching
response match {
  case Response(_) ~ Answers(OPTRecord(r) :: Nil) => ()
}
```

#### NSRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeNS) ~ Answers(NSRecord("example.com"))

// Matching
response match {
  case Response(_) ~ Answers(NSRecord(r) :: Nil) => (r.nsdname)
}
```

#### PTRRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypePTR) ~ Answers(PTRRecord("example.com"))

// Matching
response match {
  case Response(_) ~ Answers(PTRRecord(r) :: Nil) => (r.ptrdname)
}
```

#### HInfoRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeHINFO) ~ Answers(HInfoRecord("CPU", "Linux"))

// Matching
response match {
  case Response(_) ~ Answers(HInfoRecord(r) :: Nil) => (r.cpu, r.os)
}
```

#### TXTRecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeTXT) ~ Answers(TXTRecord("Test", "test", "tesT"))

// Matching
response match {
  case Response(_) ~ Answers(TXTRecord(r) :: Nil) => (r.txt)
}
```

#### SOARecord
```scala
// Creation
val response: Message = Response ~ Questions(QName("example.com") ~ TypeSOA) ~ Answers(SOARecord("example.com", "admin.example.com", 2015122401L, 3600L, 1800L, 604800L, 600L))

// Matching
response match {
  case Response(_) ~ Answers(SOARecord(r) :: Nil) => (r.mname, r.rname, r.serial, r.refresh, r.retry, r.expire, r.minimum)
}
```

Misc
----

### DnsClassName
```scala
// Matching
val msg: Message = ???
msg match {
  case Response(_) ~ Questions(DnsClassName(qclass) :: Nil) ~ Answers(DnsClassName(aclass) :: Nil) => (qclass, aclass)
}
```

### DnsTypeName
The DnsTypeName object can be used to extract a String representation of a Question or a ResourceRecord.
```scala
// Matching
val msg: Message = ???
msg match {
  case Response(_) ~ Questions(DnsTypeName(qtype) :: Nil) ~ Answers(DnsTypeName(atype) :: Nil) => (qtype, atype)
}
```

### EDNS
```scala
// Creation
val query1: Message = Query ~ Questions(QName("example.com") ~ TypeA) ~ EDNS()
val query2: Message = Query ~ Questions(QName("example.com") ~ TypeA) ~ EDNS(4096)

// Matching
query1 match {
  case Query(_) ~ Questions(q) ~ EDNS(size) => size
}
```
