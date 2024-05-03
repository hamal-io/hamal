package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.json.SerdeModuleJsonDefault
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.repository.api.SerdeModuleJsonDomain
import io.hamal.repository.api.event.SerdeModuleJsonInternalEvent

internal val serde = Serde.json()
    .register(SerdeModuleJsonDomain)
    .register(SerdeModuleJsonDefault)
    .register(SerdeModuleJsonInternalEvent)
    .register(SerdeModuleJsonValue)
    .register(SerdeModuleJsonValueVariable)
