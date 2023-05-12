package io.hamal.lib.http

data class HttpHeaders(val mapping: Map<String, String>) {
    operator fun get(key: String): String {
        val result = mapping[key]
        require(result != null) { "No header present with $key" }
        return result
    }
}

data class HttpMutableHeaders(
    private val mapping: MutableMap<String, String> = mutableMapOf()
) {
    fun toHttpHeaders() = HttpHeaders(mapping)

    fun add(key: String, value: String) {
        mapping[key] = value
    }
}