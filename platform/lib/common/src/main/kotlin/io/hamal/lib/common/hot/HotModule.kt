package io.hamal.lib.common.hot

import io.hamal.lib.common.serialization.HotArrayAdapter
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.HotObjectAdapter
import io.hamal.lib.common.serialization.InstantAdapter
import java.time.Instant

object HotObjectModule : HotModule() {
    init {
        set(HotNode::class, HotNode.Adapter)
        set(HotObject::class, HotObjectAdapter)
        set(HotArray::class, HotArrayAdapter)
        set(Instant::class, InstantAdapter)
    }
}