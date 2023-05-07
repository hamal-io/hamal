package io.hamal.lib.core.ddd.base

interface QueryMany {
    val queryManyString: String
    val parameters: Map<String, Any>
}