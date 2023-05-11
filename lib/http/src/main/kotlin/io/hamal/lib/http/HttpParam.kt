package io.hamal.lib.http

import com.sun.net.httpserver.HttpsParameters

sealed class HttpParam<VALUE : Any>(
    val name: String,
    val value: VALUE,
    val contentType: String
){
    abstract fun toQueryString() : String
}

class HttpStringParameter(name: String, value: String) : HttpParam<String>(
    name = name,
    value = value,
    contentType = "application/text"
) {
    override fun toQueryString() : String {
        return value
    }
}