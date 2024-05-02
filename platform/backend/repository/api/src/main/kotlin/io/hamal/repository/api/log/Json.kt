package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.ValueJsonModule
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.repository.api.DomainJsonModule
import io.hamal.repository.api.event.PlatformEventJsonModule

internal val json = Json(
    GsonFactoryBuilder()
        .register(DomainJsonModule)
        .register(SerdeModule)
        .register(PlatformEventJsonModule)
        .register(ValueJsonModule)
        .register(ValueVariableJsonModule)
)