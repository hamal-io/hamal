package io.hamal.lib.domain.vo

import io.hamal.lib.ddd.base.ValueObject


fun main() {
//    println("TEST")
//    val id: JobId = JobId(UUID.randomUUID().toString())
//    val y = id.resultClass
//
//
//    println(y)


//    val x = NewId(UUID.randomUUID().toString())
//    val x = NewJobId(UUID.randomUUID().toString())
//    println(x)
//    println(x.valueClass)

    data class SomeEntity(val id: JobId)

    val source = SomeEntity(JobId("ABC"))
    val target = SomeEntity(JobId("DEF"))


}