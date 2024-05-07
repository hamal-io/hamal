package io.hamal.repository.api.log

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueHon
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.repository.api.SerdeModuleDomain
import io.hamal.repository.api.event.SerdeModuleJsonInternalEvent

internal val serde = Serde.hon()
    .register(SerdeModuleDomain)
    .register(SerdeModuleJsonInternalEvent)
    .register(SerdeModuleValueHon)
    .register(SerdeModuleValueVariable)
