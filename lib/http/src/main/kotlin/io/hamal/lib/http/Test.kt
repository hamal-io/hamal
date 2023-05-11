package io.hamal.lib.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

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

@OptIn(ExperimentalSerializationApi::class)
fun main() {
//    val client = HttpClientBuilder.create()
//        .build()
//
//    val get = HttpGet("https://reqres.in/api/users?page=2")
//    val response = client.execute(get)
//    val r = Json {
//        ignoreUnknownKeys = true
//    }.decodeFromStream<Result>(response.entity.content)
//
//    println(r)

    val client = DefaultHttpClient()

    val response = client.get("https://reqres.in/api/users?page=2")
        .execute()

    val serde = KotlinJsonSerde
    val r = serde.deserialize(response.content, Result::class)


    println(response)
    println(r)
}