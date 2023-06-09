package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.ast.Expression

abstract class BuiltinFuncValue : Value {
    override val metaTable: MetaTable = DefaultBuiltinFuncMetaTable
    abstract operator fun invoke(ctx: Context): Value

    data class Context(
        val parameters: List<Parameter>,
        val env: EnvValue
    )

    data class Parameter(
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

}

object DefaultBuiltinFuncMetaTable : MetaTable {
    override val type: String = "builtin_func"
    override val operators: List<ValueOperator> = listOf()

}