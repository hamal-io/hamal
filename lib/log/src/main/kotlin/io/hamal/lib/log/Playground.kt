package io.hamal.lib.log

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray


@Serializable
data class Project(val name: String, val language: String)

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val data = Project("kotlinx.serialization", "Kotlin")
    val bytes = Cbor.encodeToByteArray(data)
    println(bytes.size)
    val obj = Cbor.decodeFromByteArray<Project>(bytes)
    println(obj)
}