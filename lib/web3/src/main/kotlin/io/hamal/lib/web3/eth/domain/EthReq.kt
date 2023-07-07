package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64

sealed interface EthGetBlockRequest

data class EthGetBlockByHashRequest(
    val hash: EthHash
) : EthGetBlockRequest

data class EthGetBlockByNumberRequest(
    val number: EthUint64
) : EthGetBlockRequest