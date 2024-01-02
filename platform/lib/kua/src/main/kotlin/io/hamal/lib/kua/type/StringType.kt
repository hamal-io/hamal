package io.hamal.lib.kua.type


data class StringType(val value: String) : SerializableType() {
    override fun toString(): String = value
}