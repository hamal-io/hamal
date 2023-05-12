package io.hamal.lib.http

class HttpParameter(
    val key: String,
    val value: String
) {

    constructor(name: String, value: Number) : this(name, value.toString())

    fun toQueryString(): String {
        return value
    }
}

fun List<HttpParameter>.toQueryString(): String {
    if (isEmpty()) {
        return ""
    }
    val builder = StringBuilder()
    builder.append("?")
    forEach { param ->
        builder.append(param.key)
        builder.append("=")
        builder.append(param.toQueryString())
        builder.append(",")
        builder.deleteCharAt(builder.length - 1)
        builder.append("&")
    }
    builder.deleteCharAt(builder.length - 1)
    return builder.toString()
}
