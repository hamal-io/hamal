package io.hamal.lib.typesystem.type

data object TypeBoolean : Type() {
    override val identifier = TypeIdentifier("Boolean")
}

data object TypeListBoolean : TypeList() {
    override val identifier = TypeIdentifier("List_Boolean")
    override val valueType = TypeBoolean
}