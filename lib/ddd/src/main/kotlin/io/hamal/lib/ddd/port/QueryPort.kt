package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.Query

interface QueryPort<RESULT : Any, SUPER_QUERY : Query> {
    fun <QUERY : SUPER_QUERY?> query(query: QUERY): List<RESULT>
    fun <QUERY : SUPER_QUERY?> findOne(query: QUERY): RESULT? {
        return query(query).firstOrNull()
    }
}
