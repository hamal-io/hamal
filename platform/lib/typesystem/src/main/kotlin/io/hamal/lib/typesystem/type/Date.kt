package io.hamal.lib.typesystem.type

data object TypeDate : Type() {
    override val identifier = TypeIdentifier("Date")
}

data object TypeListDate : TypeList() {
    override val identifier = TypeIdentifier("List_Date")
    override val valueType = TypeNumber
}