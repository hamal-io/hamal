package io.hamal.lib.typesystem.type

data object TypeString : Type() {
    override val identifier = TypeIdentifier("String")
}

data object TypeListString : TypeList() {
    override val identifier = TypeIdentifier("List_String")
    override val valueType = TypeString
}