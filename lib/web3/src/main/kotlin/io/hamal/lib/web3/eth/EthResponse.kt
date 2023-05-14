package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.domain.EthBlock

sealed interface EthResponse

data class EthBlockResponse(
    val result: EthBlock
) : EthResponse