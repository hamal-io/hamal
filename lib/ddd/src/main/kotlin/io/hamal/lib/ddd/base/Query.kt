package io.hamal.lib.ddd.base

interface Query {
    val queryString: String
    val parameters: Map<String, Any>
}