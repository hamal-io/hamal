package io.hamal.lib.typesystem.type

data object TypeDateTime : Type() {
    override val identifier = TypeIdentifier("datetime")
}

data object TypeListDateTime : TypeList() {
    override val identifier = TypeIdentifier("list_datetime")
    override val valueType = TypeNumber
}