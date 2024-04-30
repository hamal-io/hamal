package io.hamal.lib.value

data object TypeString : Type() {
    override val identifier = TypeIdentifier("String")
}

data object TypeListString : TypeList() {
    override val identifier = TypeIdentifier("List_String")
    override val valueType = TypeString
}

@JvmInline
value class ValueString(private val value: String) : Value {
    override val type get() = TypeString
    override fun toString(): String = value

    val stringValue: String get() = value
}
