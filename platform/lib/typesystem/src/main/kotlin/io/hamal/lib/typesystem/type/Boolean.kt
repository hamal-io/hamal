package io.hamal.lib.typesystem.type

data object TypeBoolean : Type() {
    override val identifier = TypeIdentifier("boolean")
}

data object TypeListBoolean : TypeList() {
    override val identifier = TypeIdentifier("list_boolean")
    override val valueType = TypeBoolean
}