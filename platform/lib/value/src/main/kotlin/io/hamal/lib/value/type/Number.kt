package io.hamal.lib.value.type

data object TypeNumber : Type() {
    override val identifier = TypeIdentifier("Number")
}

data object TypeListNumber : TypeList() {
    override val identifier = TypeIdentifier("List_Number")
    override val valueType = TypeNumber
}