package io.hamal.lib.domain.ddd.base

interface QueryMany {
    val queryManyString: String
    val parameters: Map<String, Any>
}