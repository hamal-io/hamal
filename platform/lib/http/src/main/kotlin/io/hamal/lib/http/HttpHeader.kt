package io.hamal.lib.http

data class HttpHeaders(val mapping: Map<String, String>) {
    operator fun get(key: String): String {
        val result = mapping[key]
        require(result != null) { "No header present with $key" }
        return result
    }

    operator fun get(key: String, defaultValue: String): String {
        return mapping[key] ?: defaultValue
    }
}

data class HttpMutableHeaders(
    private val mapping: MutableMap<String, String> = mutableMapOf()
) {
    fun toHttpHeaders() = HttpHeaders(mapping)

    operator fun set(key: String, value: String): HttpMutableHeaders {
        mapping[key] = value
        return this
    }
}