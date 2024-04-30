package io.hamal.lib.value.type

data object TypeTime : Type() {
    override val identifier = TypeIdentifier("Time")
}

data object TypeListTime : TypeList() {
    override val identifier = TypeIdentifier("List_Time")
    override val valueType = TypeNumber
}