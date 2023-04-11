package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token

interface Node {
    val token: Token

    fun accept(visitor: Visitor)
}