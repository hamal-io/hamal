package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeDateTime : Type() {
    override val identifier = TypeIdentifier("Date_Time")
}

data object TypeListDateTime : TypeList() {
    override val identifier = TypeIdentifier("List_Date_Time")
    override val valueType = TypeNumber
}