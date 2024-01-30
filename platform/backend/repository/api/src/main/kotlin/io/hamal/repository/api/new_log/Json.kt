package io.hamal.repository.api.new_log

import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.kua.type.KuaJsonModule
import io.hamal.repository.api.DomainJsonModule
import io.hamal.repository.api.event.PlatformEventJsonModule

internal val json = Json(
    JsonFactoryBuilder()
        .register(DomainJsonModule)
        .register(HotJsonModule)
        .register(KuaJsonModule)
        .register(PlatformEventJsonModule)
        .register(ValueObjectJsonModule)
)