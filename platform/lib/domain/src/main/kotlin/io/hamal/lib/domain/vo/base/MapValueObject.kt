package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.domain.ValueObject
import io.hamal.lib.kua.type.MapType

abstract class MapValueObject : ValueObject.BaseImpl<MapType>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}
