package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.ast.Expression

data class FuncContext(
    val params: List<FuncParam>,
    val env: EnvValue
)

data class FuncParam(
    val value: Value,
    val expression: Expression
) {
    fun asIdentifier(): IdentValue {
        return when (value) {
            is StringValue -> IdentValue(value = value.value)
            is IdentValue -> value
            else -> throw IllegalStateException("$value can not interpreted as ident")
        }
    }
}

abstract class FuncValue : Value {
    override val metaTable = DefaultFuncValueMetaTable
    abstract operator fun invoke(ctx: FuncContext): Value

}

object DefaultFuncValueMetaTable : MetaTable<FuncValue> {
    override val type = "func"
    override val operators: List<ValueOperator> = listOf()
    override val props: Map<IdentValue, ValueProp<FuncValue>> = mapOf()
}