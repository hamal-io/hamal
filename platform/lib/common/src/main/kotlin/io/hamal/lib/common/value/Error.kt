package io.hamal.lib.common.value

data object TypeError : Type() {
    override val identifier = TypeIdentifier("Error")
}


@JvmInline
value class ValueError(private val value: String) : Value {
    override val type get() = TypeError
    override fun toString(): String = value
    val stringValue: String get() = value
}