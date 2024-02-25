package io.hamal.lib.kua.type

import io.hamal.lib.kua.State

interface KuaTable : KuaType {
    val index: Int
    val state: State
}

