package io.hamal.lib.script.impl.value

import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.Block

data class PrototypeValue(
    val identifier: StringValue,
    val parameters: List<StringValue>,
    val block: Block
) : Value {

}