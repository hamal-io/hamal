package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeDateTime : Type() {
    override val identifier = TypeIdentifier("Date_Time")
}
