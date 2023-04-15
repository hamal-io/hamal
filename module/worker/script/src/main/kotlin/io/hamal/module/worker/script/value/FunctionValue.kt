package io.hamal.module.worker.script.value

import io.hamal.module.worker.script.ast.stmt.Block

data class FunctionValue(
    val identifier: StringValue,
    val parameters: List<StringValue>,
    val block: Block
) : Value {

}