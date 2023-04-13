package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Statement

class BlockStatement(val statements: List<Statement>) : Statement {
}