package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.serialization.HotArrayAdapter
import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.HotObjectAdapter

object HotObjectModule : HotModule() {
    init {
        set(SerdeNode::class, SerdeNode.Adapter)
        set(SerdeObject::class, HotObjectAdapter)
        set(SerdeArray::class, HotArrayAdapter)
    }
}