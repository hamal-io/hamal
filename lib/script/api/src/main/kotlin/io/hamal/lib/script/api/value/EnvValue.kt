package io.hamal.lib.script.api.value

class EnvValue(
    val ident: IdentValue,
    values: TableValue? = null,
    global: EnvValue? = null,
    parent: EnvValue? = null
) : Value {

    override val metaTable: MetaTable = DefaultEnvValueMetaTable

    internal val global: EnvValue
    private val parent: EnvValue
    private val values = TableValue()

    init {
        this.global = global ?: this
        this.parent = parent ?: this
        values?.let(this.values::setAll)
    }

    fun enterScope(): EnvValue {
        return EnvValue(
            ident = ident,
            values = TableValue(),
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

    operator fun get(identValue: String): Value = get(IdentValue(identValue))

    fun find(identValue: IdentValue): Value? {
        return if (parent == this) values[identValue]
        else values[identValue].let { if (it == NilValue) parent.find(identValue) else it }
    }

    fun findEnvironmentValue(identValue: IdentValue): EnvValue? {
        val result = find(identValue)
        return if (result is EnvValue) {
            result
        } else {
            null
        }
    }
}

object DefaultEnvValueMetaTable : MetaTable {
    override val type = "env"
    override val operators = listOf<ValueOperator>()
}