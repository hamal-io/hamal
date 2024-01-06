package io.hamal.lib.domain.vo.base

import io.hamal.lib.common.domain.ValueObject
import io.hamal.lib.kua.type.KuaMap

abstract class MapValueObject : ValueObject.BaseImpl<KuaMap>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}
