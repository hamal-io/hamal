package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.repository.api.SerdeModuleDomain
import io.hamal.repository.api.event.InternalEventJsonModule

internal val serde = Serde(
    GsonFactoryBuilder()
        .register(SerdeModuleDomain)
        .register(SerdeModule)
        .register(InternalEventJsonModule)
        .register(SerdeModuleJsonValue)
        .register(SerdeModuleJsonValueVariable)
)