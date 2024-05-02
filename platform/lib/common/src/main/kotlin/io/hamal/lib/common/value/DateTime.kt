package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeDateTime : TypePrimitive() {
    override val identifier = TypeIdentifier("Date_Time")
}
