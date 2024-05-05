package io.hamal.api.http.controller

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueJson
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.lib.sdk.api.SerdeModuleJsonApi

internal val json = Serde.json()
    .register(SerdeModuleJsonApi)
    .register(SerdeModuleValueJson)
    .register(SerdeModuleValueVariable)