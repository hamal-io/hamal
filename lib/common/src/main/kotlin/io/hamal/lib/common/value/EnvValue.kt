package io.hamal.lib.common.value

class EnvValue(
    val ident: IdentValue,
    values: Map<IdentValue, Value>? = null,
    global: EnvValue? = null,
    parent: EnvValue? = null
) : Value {

    override val metaTable: MetaTable = DefaultEnvMetaTable

    internal val global: EnvValue
    internal val parent: EnvValue

    private val values = mutableMapOf<IdentValue, Value>()

    init {
        this.global = global ?: this
        this.parent = parent ?: this
        values?.let(this.values::putAll)
    }

    fun enterScope(): EnvValue {
        return EnvValue(
            ident = ident,
            values = mutableMapOf(),
            global = this.global,
            parent = this
        )
    }

    fun leaveScope(): EnvValue {
        return parent
    }


    fun addLocal(identValue: IdentValue, value: Value) {
        values[identValue] = value
    }

    fun addGlobal(identValue: IdentValue, value: Value) {
        global.values[identValue] = value
    }

    operator fun get(identValue: IdentValue): Value {
        return find(identValue) ?: NilValue
    }

    operator fun get(identValue: String) = get(IdentValue(identValue))

    fun find(identValue: IdentValue): Value? {
        return if (parent == this) values[identValue]
        else values[identValue] ?: parent.find(identValue)
    }

//    fun findFunctionValue(identValue: IdentValue): FunctionValue? {
//        val result = find(identValue)
//        return if (result is FunctionValue) {
//            result
//        } else {
//            null
//        }
//    }

    fun findEnvironmentValue(identValue: IdentValue): EnvValue? {
        val result = find(identValue)
        return if (result is EnvValue) {
            result
        } else {
            null
        }
    }

//    fun findProtoTypeValue(identValue: IdentValue): PrototypeValue? {
//        val result = find(identValue)
//        return if (result is PrototypeValue) {
//            result
//        } else {
//            null
//        }
//    }

}

object DefaultEnvMetaTable : MetaTable {
    override val type = "env"
    override val operators = listOf<ValueOperator>()
}