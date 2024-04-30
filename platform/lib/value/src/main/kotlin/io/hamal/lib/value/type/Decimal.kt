package io.hamal.lib.value.type

data object TypeDecimal : Type() {
    override val identifier = TypeIdentifier("Decimal")
}

data object TypeListDecimal : TypeList() {
    override val identifier = TypeIdentifier("List_Decimal")
    override val valueType = TypeDecimal
}