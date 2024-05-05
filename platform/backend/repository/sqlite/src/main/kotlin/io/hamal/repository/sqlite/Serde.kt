package io.hamal.repository.sqlite

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleValueHon
import io.hamal.lib.domain.vo.SerdeModuleValueVariable
import io.hamal.repository.api.SerdeModuleDomain
import io.hamal.repository.record.SerdeModuleRecord

internal val hon = Serde.hon()
    .register(SerdeModuleDomain)
    .register(SerdeModuleRecord)
    .register(SerdeModuleValueHon)
    .register(SerdeModuleValueVariable)