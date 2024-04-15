package io.hamal.lib.typesystem.type

data object TypeDate : Type() {
    override val identifier = TypeIdentifier("date")
}

data object TypeListDate : TypeList() {
    override val identifier = TypeIdentifier("list_date")
    override val valueType = TypeNumber
}