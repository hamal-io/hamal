package io.hamal.lib.http

import java.io.File

sealed interface HttpBody {
    val name: String
    val contentType: String
}

data class HttpFileBody(
    override val name: String,
    override val contentType: String,
    val content: File
) : HttpBody

data class HttpByteArrayBody(
    override val name: String,
    override val contentType: String,
    val content: ByteArray
) : HttpBody

data class HttpStringBody(
    override val name: String,
    override val contentType: String,
    val content: String
) : HttpBody