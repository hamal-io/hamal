package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.ValueInstant
import io.hamal.lib.common.value.ValueVariableInstant
import java.time.Instant


class ExpiresAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        fun ExpiresAt(value: Instant) = ExpiresAt(ValueInstant(value))
    }
}