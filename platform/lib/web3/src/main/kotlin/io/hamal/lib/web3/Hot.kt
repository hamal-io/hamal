package io.hamal.lib.web3

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.lib.web3.evm.SerdeModuleJsonEvm
import io.hamal.lib.web3.evm.chain.arbitrum.SerdeModuleJsonArbitrum
import io.hamal.lib.web3.evm.chain.eth.SerdeModuleJsonEth


val serde = Serde.json()
    .register(SerdeModule)
    .register(SerdeModuleJsonValue)
    .register(SerdeModuleJsonValueVariable)
    .register(SerdeModuleJsonEvm)
    .register(SerdeModuleJsonEth)
    .register(SerdeModuleJsonArbitrum)