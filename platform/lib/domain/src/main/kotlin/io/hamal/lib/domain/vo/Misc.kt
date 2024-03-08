package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectInstant
import java.time.Instant

class ExpiresAt(override val value: Instant) : ValueObjectInstant()