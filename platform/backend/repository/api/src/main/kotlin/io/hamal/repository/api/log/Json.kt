package io.hamal.repository.api.log

import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.repository.api.DomainJsonModule
import io.hamal.repository.api.event.PlatformEventJsonModule

internal val json = Json(
    JsonFactoryBuilder()
        .register(DomainJsonModule)
        .register(HotObjectModule)
        .register(PlatformEventJsonModule)
        .register(ValueVariableJsonModule)
)