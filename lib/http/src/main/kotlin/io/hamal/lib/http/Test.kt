package io.hamal.lib.http

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val data: List<Person>
)

@Serializable
data class Person(
    val id: Int,
    val email: String
)

fun main() {
    val client = DefaultHttpClient()

    val r = client.get("https://reqres.in/api/users")
        .execute(Result::class)

//    println(response)
//
//    val serde = KotlinJsonHttpContentDeserializer
//    val r = serde.deserialize(response.inputStream, Result::class)
//


    println(r)

    r.data.forEach {
        println(it)
    }
}