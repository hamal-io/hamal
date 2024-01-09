package io.hamal.repository.api.log

import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.InvocationModule
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.kua.type.KuaJsonModule

internal val json = Json(
    JsonFactoryBuilder()
        .register(HotJsonModule)
        .register(InvocationModule)
        .register(KuaJsonModule)
        .register(ValueObjectJsonModule)
)