package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.domain.ValueObject
import java.time.Instant

abstract class DomainAt : ValueObject.ComparableImpl<Instant>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

