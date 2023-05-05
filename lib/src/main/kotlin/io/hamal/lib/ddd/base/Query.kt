package io.hamal.lib.ddd.base

interface QueryMany {
    val queryManyString: String
    val parameters: Map<String, Any>
}