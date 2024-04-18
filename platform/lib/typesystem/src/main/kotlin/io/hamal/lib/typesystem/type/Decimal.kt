package io.hamal.lib.typesystem.type

data object TypeDecimal : Type() {
    override val identifier = TypeIdentifier("Decimal")
}

data object TypeListDecimal : TypeList() {
    override val identifier = TypeIdentifier("List_Decimal")
    override val valueType = TypeDecimal
}