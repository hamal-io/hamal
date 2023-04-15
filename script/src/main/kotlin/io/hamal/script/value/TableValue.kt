package io.hamal.script.value

class TableValue : Value {

    private val store = mutableMapOf<Value, Value>()

    operator fun set(key: Int, value: Value) {
        this[NumberValue(key)] = value
    }

    operator fun set(key: Value, value: Value) {
        store[key] = value
    }

    operator fun get(key: Int): Value = store[NumberValue(key)] ?: NilValue
    operator fun get(key: Value): Value = store[key] ?: NilValue

    fun remove(key: Int) {
        remove(NumberValue(key))
    }

    fun remove(key: Value) {
        store.remove(key)
    }

    fun size() = store.size
}