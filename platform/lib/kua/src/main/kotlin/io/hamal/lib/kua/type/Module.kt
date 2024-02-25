package io.hamal.lib.kua.type

import io.hamal.lib.common.serialization.JsonModule

object KuaJsonModule : JsonModule() {
    init {
        this[KuaAny::class] = KuaAny.Adapter
        this[KuaDecimal::class] = KuaDecimal.Adapter
        this[KuaBoolean::class] = KuaBoolean.Adapter
        this[KuaTableType::class] = KuaTableType.Adapter
        this[KuaNil::class] = KuaNil.Adapter
        this[KuaType::class] = KuaType.Adapter
    }
}