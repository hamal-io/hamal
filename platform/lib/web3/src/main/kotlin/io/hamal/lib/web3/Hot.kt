package io.hamal.lib.web3

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.serde.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.lib.web3.evm.SerdeModuleJsonEvm
import io.hamal.lib.web3.evm.chain.arbitrum.SerdeModuleJsonArbitrum
import io.hamal.lib.web3.evm.chain.eth.SerdeModuleJsonEth


val serde = Serde.json()
    .register(SerdeModuleJsonValue)
    .register(SerdeModuleJsonValueVariable)
    .register(SerdeModuleJsonEvm)
    .register(SerdeModuleJsonEth)
    .register(SerdeModuleJsonArbitrum)