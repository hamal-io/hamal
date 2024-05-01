package io.hamal.lib.common.value

data object TypeCode : Type() {
    override val identifier = TypeIdentifier("Code")
}


@JvmInline
value class ValueCode(private val value: String) : Value {
    override val type get() = TypeCode
    override fun toString(): String = value
    val stringValue: String get() = value
}