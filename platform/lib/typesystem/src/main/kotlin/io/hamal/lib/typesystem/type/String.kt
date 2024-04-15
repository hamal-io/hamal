package io.hamal.lib.typesystem.type

data object TypeString : Type() {
    override val identifier = TypeIdentifier("string")
}

data object TypeListString : TypeList() {
    override val identifier = TypeIdentifier("list_string")
    override val valueType = TypeString
}