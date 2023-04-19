package io.hamal.script.impl.value

import io.hamal.script.api.value.StringValue
import io.hamal.script.api.value.Value
import io.hamal.script.impl.ast.stmt.Block

data class PrototypeValue(
    val identifier: StringValue,
    val parameters: List<StringValue>,
    val block: Block
) : Value {

}