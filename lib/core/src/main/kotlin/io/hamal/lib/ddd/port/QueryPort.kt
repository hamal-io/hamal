package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.QueryMany

interface QueryManyPort<RESULT : Any, SUPER_QUERYMANY : QueryMany> {
    fun <QUERYMANY : SUPER_QUERYMANY?> queryMany(queryMany: QUERYMANY): List<RESULT>
    fun <QUERYMANY : SUPER_QUERYMANY?> findOne(queryMany: QUERYMANY): RESULT? {
        return queryMany(queryMany).firstOrNull()
    }
}
