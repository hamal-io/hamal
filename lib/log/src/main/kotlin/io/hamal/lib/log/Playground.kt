package io.hamal.lib.log

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf


@Serializable
data class Project(val name: String, val language: String)

@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val data = Project("kotlinx.serialization", "Kotlin")
    val bytes = ProtoBuf.encodeToByteArray(data)
    println(bytes.size)
    println(bytes.joinToString(separator = "") { eachByte -> "%02x".format(eachByte) })
    val obj = ProtoBuf.decodeFromByteArray<Project>(bytes)
    println(obj)
}