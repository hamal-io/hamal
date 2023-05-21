package io.hamal.lib.script.api.value

class EnvironmentValue(
    val identifier: Identifier,
    values: Map<Identifier, Value>? = null,
    global: EnvironmentValue? = null,
    parent: EnvironmentValue? = null
) : Value {

    override val metaTable: MetaTable = MetaTableNotImplementedYet

    internal val global: EnvironmentValue
    internal val parent: EnvironmentValue

    private val values = mutableMapOf<Identifier, Value>()

    init {
        this.global = global ?: this
        this.parent = parent ?: this
        values?.let(this.values::putAll)
    }

    fun enterScope(): EnvironmentValue {
        return EnvironmentValue(
            identifier = identifier,
            values = mutableMapOf(),
            global = this.global,
            parent = this
        )
    }

    fun leaveScope(): EnvironmentValue {
        return parent
    }


    fun addLocal(identifier: Identifier, value: Value) {
        values[identifier] = value
    }

    fun addGlobal(identifier: Identifier, value: Value) {
        global.values[identifier] = value
    }

    operator fun get(identifier: Identifier): Value {
        return find(identifier) ?: NilValue
    }

    operator fun get(identifier: String) = get(Identifier(identifier))

    fun find(identifier: Identifier): Value? {
        return if (parent == this) values[identifier]
        else values[identifier] ?: parent.find(identifier)
    }

    fun findFunctionValue(identifier: Identifier): FunctionValue? {
        val result = find(identifier)
        return if (result is FunctionValue) {
            result
        } else {
            null
        }
    }

    fun findEnvironmentValue(identifier: Identifier): EnvironmentValue? {
        val result = find(identifier)
        return if (result is EnvironmentValue) {
            result
        } else {
            null
        }
    }

    fun findProtoTypeValue(identifier: Identifier): PrototypeValue? {
        val result = find(identifier)
        return if (result is PrototypeValue) {
            result
        } else {
            null
        }
    }

}