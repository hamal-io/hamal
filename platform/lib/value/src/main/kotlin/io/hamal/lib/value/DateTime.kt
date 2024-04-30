package io.hamal.lib.value

data object TypeDateTime : Type() {
    override val identifier = TypeIdentifier("Date_Time")
}

data object TypeListDateTime : TypeList() {
    override val identifier = TypeIdentifier("List_Date_Time")
    override val valueType = TypeNumber
}