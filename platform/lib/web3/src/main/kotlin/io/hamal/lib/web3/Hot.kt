package io.hamal.lib.web3

import io.hamal.lib.common.serialization.GsonFactoryBuilder
import io.hamal.lib.common.serialization.json.SerdeModule
import io.hamal.lib.common.value.ValueJsonModule
import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.web3.evm.EvmHotModule
import io.hamal.lib.web3.evm.chain.arbitrum.ArbitrumHotModule
import io.hamal.lib.web3.evm.chain.eth.EthHotModule


val serde = Serde(
    GsonFactoryBuilder()
        .register(SerdeModule)
        .register(ValueJsonModule)
        .register(ValueVariableJsonModule)
        .register(EvmHotModule)
        .register(EthHotModule)
        .register(ArbitrumHotModule)

)