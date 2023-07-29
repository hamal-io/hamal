package io.hamal.app.proxy.domain

import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64

data class EthCall(
    val blockId: EthUint64,
    val to: EthAddress,
    val data: EthPrefixedHexString,
    val result: EthPrefixedHexString
)