package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.domain.vo.SerdeModuleValueVariableJson
import io.hamal.repository.api.SerdeModuleJsonDomain
import io.hamal.repository.api.event.SerdeModuleJsonInternalEvent

internal val serde = Serde.json()
    .register(SerdeModuleJsonDomain)
    .register(SerdeModuleJsonInternalEvent)
    .register(SerdeModuleValueJson)
    .register(SerdeModuleValueVariableJson)
