package io.hamal.lib.script.api.value

class DepEnvironmentValue(
    val identifier: DepIdentifier,
    values: Map<DepIdentifier, DepValue>? = null,
    global: DepEnvironmentValue? = null,
    parent: DepEnvironmentValue? = null
) : DepValue {

    override val metaTable: DepMetaTable = DepMetaTableNotImplementedYet

    internal val global: DepEnvironmentValue
    internal val parent: DepEnvironmentValue

    private val values = mutableMapOf<DepIdentifier, DepValue>()

    init {
        this.global = global ?: this
        this.parent = parent ?: this
        values?.let(this.values::putAll)
    }

    fun enterScope(): DepEnvironmentValue {
        return DepEnvironmentValue(
            identifier = identifier,
            values = mutableMapOf(),
            global = this.global,
            parent = this
        )
    }

    fun leaveScope(): DepEnvironmentValue {
        return parent
    }


    fun addLocal(identifier: DepIdentifier, value: DepValue) {
        values[identifier] = value
    }

    fun addGlobal(identifier: DepIdentifier, value: DepValue) {
        global.values[identifier] = value
    }

    operator fun get(identifier: DepIdentifier): DepValue {
        return find(identifier) ?: DepNilValue
    }

    operator fun get(identifier: String) = get(DepIdentifier(identifier))

    fun find(identifier: DepIdentifier): DepValue? {
        return if (parent == this) values[identifier]
        else values[identifier] ?: parent.find(identifier)
    }

    fun findFunctionValue(identifier: DepIdentifier): DepFunctionValue? {
        val result = find(identifier)
        return if (result is DepFunctionValue) {
            result
        } else {
            null
        }
    }

    fun findEnvironmentValue(identifier: DepIdentifier): DepEnvironmentValue? {
        val result = find(identifier)
        return if (result is DepEnvironmentValue) {
            result
        } else {
            null
        }
    }

    fun findProtoTypeValue(identifier: DepIdentifier): DepPrototypeValue? {
        val result = find(identifier)
        return if (result is DepPrototypeValue) {
            result
        } else {
            null
        }
    }

}