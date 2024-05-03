package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.repository.api.SerdeModuleJsonDomain
import io.hamal.repository.api.event.SerdeModuleJsonInternalEvent

internal val serde = Serde.json()
    .register(SerdeModuleJsonDomain)
    .register(SerdeModuleJsonInternalEvent)
    .register(SerdeModuleJsonValue)
    .register(SerdeModuleJsonValueVariable)
