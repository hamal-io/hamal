package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.EthAddress
import io.hamal.lib.web3.eth.abi.EthHash
import io.hamal.lib.web3.eth.abi.EthUint64


data class EthBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val miner: EthAddress,
    val timestamp: EthUint64
)