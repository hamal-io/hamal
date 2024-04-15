package io.hamal.lib.typesystem.type

data object TypeNumber : Type() {
    override val identifier = TypeIdentifier("number")
}

data object TypeListNumber : TypeList() {
    override val identifier = TypeIdentifier("list_number")
    override val valueType = TypeNumber
}