package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type.Code


data class KuaCode(val value: String) : KuaType {
    constructor(str: KuaString) : this(str.value)

    override val type: KuaType.Type = Code
}