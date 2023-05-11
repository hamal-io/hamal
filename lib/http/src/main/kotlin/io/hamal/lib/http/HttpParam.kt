package io.hamal.lib.http

sealed class HttpParam<VALUE : Any>(
    val name: String,
    val value: VALUE,
    val contentType: String
) {
    abstract fun toQueryString(): String
}

class HttpStringParameter(name: String, value: String) : HttpParam<String>(
    name = name,
    value = value,
    contentType = "application/text"
) {
    override fun toQueryString(): String {
        return value
    }
}