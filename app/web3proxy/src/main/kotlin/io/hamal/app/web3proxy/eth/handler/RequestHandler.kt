package io.hamal.app.web3proxy.eth.handler

import io.hamal.app.web3proxy.eth.repository.EthBlockDataRepository
import io.hamal.lib.web3.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthRequest
import io.hamal.lib.web3.eth.domain.EthResponse

interface HandleEthRequest {
    operator fun invoke(request: EthRequest): EthResponse
    operator fun invoke(requests: List<EthRequest>): List<EthResponse>
}

class EthRequestHandlerImpl(
    private val blockDataRepository: EthBlockDataRepository,
) : HandleEthRequest {

    override fun invoke(request: EthRequest): EthResponse {
        return invoke(listOf(request)).firstOrNull()
            ?: throw RuntimeException("Unable to process request")
    }

    override fun invoke(requests: List<EthRequest>): List<EthResponse> {
        val blocks = requests.filterIsInstance<EthGetBlockByNumberRequest>().map { it.number }
        val getBlockNumberRequestIds = requests.filterIsInstance<EthGetBlockByNumberRequest>()
            .associate { it.number to it.id }

        return blockDataRepository.list(blocks).map { block ->
            EthGetBlockResponse(
                id = getBlockNumberRequestIds[block.number]!!,
                result = block
            )
        }
    }
}