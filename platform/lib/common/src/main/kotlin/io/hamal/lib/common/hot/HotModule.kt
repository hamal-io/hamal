package io.hamal.lib.common.hot

import io.hamal.lib.common.serialization.HotArrayAdapter
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.HotObjectAdapter

object HotObjectModule : HotModule() {
    init {
        set(HotNode::class, HotNode.Adapter)
        set(HotObject::class, HotObjectAdapter)
        set(HotArray::class, HotArrayAdapter)
    }
}