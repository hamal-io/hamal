package io.hamal.lib.web3.eth.http

import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.EthBlockResponse
import io.hamal.lib.web3.eth.EthResponse
import io.hamal.lib.web3.eth.abi.EthAddress
import io.hamal.lib.web3.eth.abi.EthBytes32
import io.hamal.lib.web3.eth.abi.EthHash
import io.hamal.lib.web3.eth.abi.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import java.math.BigInteger

class EthHttpBatchService : EthBatchService<EthHttpBatchService> {
    override fun execute(): List<EthResponse> {
        return listOf(
            EthBlockResponse(
                EthBlock(
                    number = EthUint64(BigInteger.ONE),
                    hash = EthHash(EthBytes32(ByteArray(32))),
                    parentHash = EthHash(EthBytes32(ByteArray(32))),
                    miner = EthAddress(BigInteger.ZERO),
                    timestamp = EthUint64(BigInteger.ZERO      )
                )
            )
        )
    }

    override fun getBlock(req: EthBatchService.GetBlockRequest): EthHttpBatchService {
        return this
    }

}