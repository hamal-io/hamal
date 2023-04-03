package io.hamal.lib.ddd.port

import io.hamal.lib.ddd.base.Query
import io.hamal.lib.meta.Maybe

interface QueryPort<RESULT : Any, SUPER_QUERY : Query> {
    fun <QUERY : SUPER_QUERY?> query(query: QUERY): List<RESULT>
    fun <QUERY : SUPER_QUERY?> findOne(query: QUERY): Maybe<RESULT>? {
        return Maybe(query(query).firstOrNull())
    }
}
