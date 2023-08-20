package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass

@JvmInline
value class StackTop(val value: Int)

interface State {
    //FIXME probably not a good idea to expose this internal - only for development / prototyping
    val native: Native
    val top: StackTop

    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun setTop(idx: Int)
    fun absIndex(idx: Int): Int
    fun pushTop(idx: Int): StackTop

    fun type(idx: Int): KClass<out Type>
    fun pushNil(): StackTop
    fun pushAny(value: AnyType): StackTop
    fun getAny(idx: Int): AnyType

    fun getArrayType(idx: Int): ArrayType

    fun pushBoolean(value: Boolean): StackTop
    fun pushBoolean(value: BooleanType) = pushBoolean(value.value)
    fun getBoolean(idx: Int): Boolean
    fun getBooleanValue(idx: Int) = booleanOf(getBoolean(idx))
    fun pushError(value: ErrorType): StackTop
    fun pushFunction(value: FunctionType<*, *, *, *>): StackTop

    fun getNumber(idx: Int): Double
    fun getNumberType(idx: Int) = NumberType(getNumber(idx))
    fun pushNumber(value: Double): StackTop
    fun pushNumber(value: NumberType) = pushNumber(value.value)

    fun getString(idx: Int): String
    fun getStringType(idx: Int) = StringType(getString(idx))
    fun pushString(value: String): StackTop
    fun pushString(value: StringType) = pushString(value.value)

    fun pushTable(value: TableType): StackTop
    fun pushTable(proxy: TableProxyMap): StackTop
    fun pushTable(proxy: TableProxyArray): StackTop
    fun getTable(idx: Int): TableType
    fun getTableMap(idx: Int): TableProxyMap
    fun getTableArray(idx: Int): TableProxyArray

    fun getMapType(idx: Int): MapType

    fun setGlobal(name: String, value: FunctionType<*, *, *, *>)
    fun setGlobal(name: String, value: TableProxyMap)
    fun setGlobal(name: String, value: TableProxyArray)
    fun getGlobalTableMap(name: String): TableProxyMap
    fun unsetGlobal(name: String)

    fun tableCreateMap(capacity: Int = 0): TableProxyMap
    fun tableCreateArray(capacity: Int = 0): TableProxyArray
    fun tableAppend(idx: Int): Int
    fun tableSetRaw(idx: Int): Int
    fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): Int
    fun tableGetRaw(idx: Int): KClass<out Type>
    fun tableGetRawIdx(stackIdx: Int, tableIdx: Int): KClass<out Type>

    fun load(code: String) // FIXME add return value
}

class ClosableState(
    override val native: Native
) : State, AutoCloseable {
    override val top: StackTop get() = StackTop(native.top())


    override fun isEmpty() = native.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = native.setTop(idx)
    override fun absIndex(idx: Int) = native.absIndex(idx)

    override fun pushTop(idx: Int): StackTop = StackTop(native.pushTop(idx))

    override fun type(idx: Int) = luaToType(native.type(idx))

    override fun pushNil() = StackTop(native.pushNil())

    override fun pushAny(value: AnyType): StackTop {
        return when (val underlying = value.value) {
            is BooleanType -> pushBoolean(underlying)
            is NumberType -> pushNumber(underlying)
            is StringType -> pushString(underlying)
            is TableProxyArray -> pushTable(underlying)
            is TableProxyMap -> pushTable(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAny(idx: Int): AnyType {
        return when (val type = type(idx)) {
            BooleanType::class -> AnyType(getBooleanValue(idx))
            NumberType::class -> AnyType(getNumberType(idx))
            StringType::class -> AnyType(getStringType(idx))
            TableType::class -> AnyType(getTableMap(idx)) // FIXME what about table and array ?
            else -> TODO("$type not supported yet")
        }
    }

    override fun getArrayType(idx: Int): ArrayType {
        val ref = TableProxyArray(absIndex(idx), this)
        return toArrayType(ref)
    }

    override fun pushBoolean(value: Boolean): StackTop = StackTop(native.pushBoolean(value))
    override fun getBoolean(idx: Int): Boolean = native.toBoolean(idx)
    override fun pushError(value: ErrorType) = StackTop(native.pushError(value.message))
    override fun pushFunction(value: FunctionType<*, *, *, *>) = StackTop(native.pushFunction(value))

    override fun getNumber(idx: Int) = native.toNumber(idx)
    override fun pushNumber(value: Double) = StackTop(native.pushNumber(value))
    override fun getString(idx: Int) = native.toString(idx)
    override fun pushString(value: String) = StackTop(native.pushString(value))

    override fun pushTable(value: TableType): StackTop {
        TODO("Not yet implemented")
    }

    override fun pushTable(proxy: TableProxyMap) = StackTop(native.pushTop(proxy.index))

    override fun pushTable(proxy: TableProxyArray) = StackTop(native.pushTop(proxy.index))

    override fun getTable(idx: Int): TableType {
        TODO("Not yet implemented")
    }

    //FIXME type check
    override fun getTableMap(idx: Int): TableProxyMap = TableProxyMap(absIndex(idx), this)

    //FIXME type check
    override fun getTableArray(idx: Int): TableProxyArray = TableProxyArray(absIndex(idx), this)

    override fun getMapType(idx: Int): MapType {
        val ref = TableProxyMap(absIndex(idx), this)
        return toMapType(ref)
    }

    override fun setGlobal(name: String, value: FunctionType<*, *, *, *>) {
        native.pushFunction(value)
        native.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableProxyMap) {
        native.pushTop(value.index)
        native.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableProxyArray) {
        native.pushTop(value.index)
        native.setGlobal(name)
    }

    override fun getGlobalTableMap(name: String): TableProxyMap {
        native.getGlobal(name)
        return getTableMap(top.value)
    }

    override fun unsetGlobal(name: String) {
        native.pushNil()
        native.setGlobal(name)
    }

    override fun tableCreateMap(capacity: Int): TableProxyMap {
        return TableProxyMap(
            index = native.tableCreate(0, capacity),
            state = this
        )
    }

    override fun tableCreateArray(capacity: Int): TableProxyArray {
        return TableProxyArray(
            index = native.tableCreate(capacity, 0),
            state = this
        )
    }

    override fun tableAppend(idx: Int) = native.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = native.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = native.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = luaToType(native.tableGetRaw(idx))
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = luaToType(native.tableGetRawIdx(stackIdx, tableIdx))

    override fun load(code: String) {
        native.load(code)
    }

    override fun close() {
        native.close()
    }
}

private fun luaToType(value: Int) = when (value) {
    0 -> NilType::class
    1 -> BooleanType::class
    3 -> NumberType::class
    4 -> StringType::class
    5 -> TableType::class
    else -> TODO("$value not implemented yet")
}
