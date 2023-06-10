package io.hamal.lib.script.api.value

abstract class FuncValue<CTX : FuncValue.Context> : Value {
    override val metaTable: MetaTable = DefaultFuncValueMetaTable
    abstract operator fun invoke(ctx: CTX): Value
    interface Context {
        val parameters: List<Value>
        val env: EnvValue
    }
}

object DefaultFuncValueMetaTable : MetaTable {
    override val type = "func"
    override val operators: List<ValueOperator> = listOf()
}