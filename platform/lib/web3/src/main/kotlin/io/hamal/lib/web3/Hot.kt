package io.hamal.lib.web3

import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.web3.evm.EvmHotModule
import io.hamal.lib.web3.evm.chain.arbitrum.ArbitrumHotModule
import io.hamal.lib.web3.evm.chain.eth.EthHotModule


val json = Json(
    JsonFactoryBuilder()
        .register(HotObjectModule)
        .register(ValueObjectJsonModule)
        .register(ValueVariableJsonModule)
        .register(EvmHotModule)
        .register(EthHotModule)
        .register(ArbitrumHotModule)

)