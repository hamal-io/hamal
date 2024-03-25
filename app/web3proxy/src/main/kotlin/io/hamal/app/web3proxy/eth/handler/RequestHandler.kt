package io.hamal.app.web3proxy.eth.handler

import io.hamal.app.web3proxy.eth.repository.EthRepository
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.impl.eth.domain.EthRequest
import io.hamal.lib.web3.evm.impl.eth.domain.EthResponse

interface HandleEthRequest {
    operator fun invoke(request: EthRequest): EthResponse
    operator fun invoke(requests: List<EthRequest>): List<EthResponse>
}

class EthRequestHandlerImpl(
    private val repository: EthRepository,
) : HandleEthRequest {

    override fun invoke(request: EthRequest): EthResponse {
        return invoke(listOf(request)).firstOrNull()
            ?: throw RuntimeException("Unable to process request")
    }

    override fun invoke(requests: List<EthRequest>): List<EthResponse> {
        return requests.filterIsInstance<EthGetBlockByNumberRequest>().let { reqs ->
            repository.listBlocks(reqs.map { it.number })
                .zip(reqs)
                .map { (block, request) ->
                    EthGetBlockResponse(
                        id = request.id,
                        result = block
                    )
                }
        }

    }
}