package io.hamal.lib.typesystem.type

data object TypeTime : Type() {
    override val identifier = TypeIdentifier("time")
}

data object TypeListTime : TypeList() {
    override val identifier = TypeIdentifier("list_time")
    override val valueType = TypeNumber
}