package io.hamal.lib.typesystem.type

data object TypeDecimal : Type() {
    override val identifier = TypeIdentifier("decimal")
}

data object TypeListDecimal : TypeList() {
    override val identifier = TypeIdentifier("list_decimal")
    override val valueType = TypeDecimal
}