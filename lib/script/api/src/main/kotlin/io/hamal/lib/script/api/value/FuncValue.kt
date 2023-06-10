package io.hamal.lib.script.api.value

interface FuncInvocationContext {
    val parameters: List<Value>
    val env: EnvValue
}

interface FuncInvocationContextFactory<INVOKE_CTX : FuncInvocationContext> {
    fun create(parameters: List<Value>, env: EnvValue): INVOKE_CTX
}

object DefaultFuncInvocationContextFactory : FuncInvocationContextFactory<DefaultFuncInvocationContext> {
        override fun create(
        parameters: List<Value>,
        env: EnvValue
    ): DefaultFuncInvocationContext = DefaultFuncInvocationContext(parameters, env)
}

data class DefaultFuncInvocationContext(
    override val parameters: List<Value>,
    override val env: EnvValue
) : FuncInvocationContext

abstract class FuncValue<CTX : FuncInvocationContext> : Value {
    override val metaTable: MetaTable = DefaultFuncValueMetaTable
    abstract operator fun invoke(ctx: CTX): Value

}

object DefaultFuncValueMetaTable : MetaTable {
    override val type = "func"
    override val operators: List<ValueOperator> = listOf()
}