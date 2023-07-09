package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64

sealed interface EthGetBlockReq
data class EthGetBlockByHashRequest(
    val hash: EthHash
) : EthGetBlockReq

data class EthGetBlockByNumberReq(
    val number: EthUint64
) : EthGetBlockReq