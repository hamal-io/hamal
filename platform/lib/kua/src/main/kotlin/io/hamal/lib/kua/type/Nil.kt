package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type.Nil


object KuaNil : KuaType {
    override val type: KuaType.Type = Nil
}