package io.hamal.lib.http

class HttpHeaders(mapping: Map<String, String>) : Map<String, String> {

    private val mapping: Map<String, String>

    init {
        this.mapping = mapping.map { it.key.lowercase() to it.value }.toMap()
    }

    override operator fun get(key: String): String {
        val result = mapping[key]
        require(result != null) { "No header present with $key" }
        return result
    }

    fun find(key: String): String? {
        return mapping[key]
    }

    override val entries = mapping.entries
    override val keys = mapping.keys
    override val size = mapping.size
    override val values = mapping.values

    override fun isEmpty() = mapping.isEmpty()
    override fun containsValue(value: String) = mapping.containsValue(value)
    override fun containsKey(key: String) = mapping.containsKey(key)
}

class HttpMutableHeaders(
    private val mapping: MutableMap<String, String> = mutableMapOf()
) {
    fun toHttpHeaders() = HttpHeaders(mapping)

    operator fun set(key: String, value: String): HttpMutableHeaders {
        mapping[key] = value
        return this
    }
}