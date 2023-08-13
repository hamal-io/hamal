package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.type.*


interface TableProxy : Type {
    val index: Int
    val type: Type

    fun length(): Int

    enum class Type {
        Array,
        Map
    }
}


data class DefaultTableProxy(
    override val index: Int,
    val state: State,
    override val type: TableProxy.Type
) : TableMap, TableArray {

    override fun unset(key: String): Int {
        native.pushString(key)
        native.pushNil()
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: Boolean): Int {
        native.pushString(key)
        native.pushBoolean(value)
        return state.tableSetRaw(index)
    }


    override fun set(key: String, value: Double): Int {
        native.pushString(key)
        native.pushNumber(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: String): Int {
        state.pushString(key)
        state.pushString(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: FunctionType<*, *, *, *>): Int {
        state.pushString(key)
        state.pushFunction(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: TableMap): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: TableArray): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    override fun getBooleanValue(key: String): BooleanType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Boolean)
        return booleanOf(native.toBoolean(state.top.value)).also { native.pop(1) }
    }

    override fun getCode(key: String): CodeType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return CodeType(native.toString(state.top.value)).also { native.pop(1) }
    }

    override fun getNumberValue(key: String): DoubleType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Number)
        return DoubleType(native.toNumber(state.top.value)).also { native.pop(1) }
    }

    override fun getStringValue(key: String): StringType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return StringType(native.toString(state.top.value)).also { native.pop(1) }
    }

    override fun getTableMap(key: String): TableMap {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Table)
        return state.getTableMap(state.top.value)
    }

    override fun length() = native.tableGetLength(index)

    override fun append(value: Boolean): Int {
        native.pushBoolean(value)
        return state.tableAppend(index)
    }

    override fun append(value: Double): Int {
        native.pushNumber(value)
        return state.tableAppend(index)
    }

    override fun append(value: String): Int {
        native.pushString(value)
        return state.tableAppend(index)
    }

    override fun append(value: TableMap): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }

    override fun append(value: TableArray): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }

    override fun get(idx: Int): AnyType {
        TODO("Not yet implemented")
    }

    override fun getBooleanValue(idx: Int): BooleanType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.Boolean)
        return state.getBooleanValue(-1)
    }

    override fun getNumberValue(idx: Int): DoubleType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.Number)
        return state.getNumberValue(-1)
    }

    override fun getStringValue(idx: Int): StringType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.String)
        return state.getStringValue(-1)
    }

    private val native = state.native
}

private fun ValueType.checkExpectedType(expected: ValueType) {
    check(this == expected) {
        "Expected type to be ${expected.toString().lowercase()} but was ${this.toString().lowercase()}"
    }
}