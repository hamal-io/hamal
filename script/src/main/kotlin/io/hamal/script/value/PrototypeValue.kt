package io.hamal.script.value

import io.hamal.script.ast.stmt.Block

data class PrototypeValue(
    val identifier: StringValue,
    val parameters: List<StringValue>,
    val block: Block
) : Value {

}