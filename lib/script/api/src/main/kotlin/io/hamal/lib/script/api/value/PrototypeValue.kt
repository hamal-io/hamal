package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.ast.Statement

data class PrototypeValue(
    val identifier: Identifier,
    val parameters: List<StringValue>,
    val block: Statement
) : Value {
    override val metaTable = MetaTableNotImplementedYet

}