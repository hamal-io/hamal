package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.ast.Statement

data class DepPrototypeValue(
    val identifier: DepIdentifier,
    val parameters: List<DepStringValue>,
    val block: Statement
) : DepValue {
    override val metaTable = DepMetaTableNotImplementedYet

}