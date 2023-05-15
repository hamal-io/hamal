package io.hamal.lib.script.impl.value

import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.script.impl.ast.stmt.BlockStatement

data class PrototypeValue(
    val identifier: Identifier,
    val parameters: List<StringValue>,
    val block: BlockStatement
) : Value {

}