package io.hamal.lib.web3

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthRequestId

object Web3JsonModule : JsonModule() {
    init {
        this[EthAddress::class] = EthType.Adapter(::EthAddress)

        this[EthHash::class] = EthType.Adapter(::EthHash)

        this[EthPrefixedHexString::class] = EthType.Adapter(::EthPrefixedHexString)
        this[EthRequestId::class] = EthRequestId.Adapter

        this[EthUint8::class] = EthType.Adapter(::EthUint8)
        this[EthUint64::class] = EthType.Adapter(::EthUint64)
        this[EthUint256::class] = EthType.Adapter(::EthUint256)
    }
}
