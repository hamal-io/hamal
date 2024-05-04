package io.hamal.lib.common.value

val TypeError = ValueType("Error")

@JvmInline
value class ValueError(private val value: String) : Value {
    override val type get() = TypeError
    override fun toString(): String = value
    val stringValue: String get() = value
}