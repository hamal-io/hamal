package io.hamal.lib.common.hot

import io.hamal.lib.common.serialization.HotArrayAdapter
import io.hamal.lib.common.serialization.HotObjectAdapter
import io.hamal.lib.common.serialization.InstantAdapter
import io.hamal.lib.common.serialization.JsonModule
import java.time.Instant

object HotJsonModule : JsonModule() {
    init {
        set(HotObject::class, HotObjectAdapter)
        set(HotArray::class, HotArrayAdapter)
        set(Instant::class, InstantAdapter)
    }
}