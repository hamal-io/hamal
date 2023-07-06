package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.ast.Statement


data class PrototypeValue(
    val ident: IdentValue,
    val parameters: List<StringValue>,
    val block: Statement
) : Value {
    override val metaTable = DefaultStringValueMetaTable
}

object DefaultPrototypeValueMetaTable : MetaTable<PrototypeValue> {
    override val type = "prototype"
    override val operators: List<ValueOperator> = listOf()
    override val props: Map<IdentValue, ValueProp<PrototypeValue>> = mapOf()
}