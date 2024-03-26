package io.hamal.app.web3proxy.arbitrum.handler

import io.hamal.app.web3proxy.arbitrum.repository.ArbitrumRepository
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumRequest
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumResponse

interface HandleArbitrumRequest {
    operator fun invoke(request: ArbitrumRequest): ArbitrumResponse
    operator fun invoke(requests: List<ArbitrumRequest>): List<ArbitrumResponse>
}

class ArbitrumRequestHandlerImpl(
    private val repository: ArbitrumRepository,
) : HandleArbitrumRequest {

    override fun invoke(request: ArbitrumRequest): ArbitrumResponse {
        return invoke(listOf(request)).firstOrNull()
            ?: throw RuntimeException("Unable to process request")
    }

    override fun invoke(requests: List<ArbitrumRequest>): List<ArbitrumResponse> {
        return requests.filterIsInstance<ArbitrumGetBlockByNumberRequest>().let { reqs ->
            repository.listBlocks(reqs.map { it.number })
                .zip(reqs)
                .map { (block, request) ->
                    ArbitrumGetBlockResponse(
                        id = request.id,
                        result = block
                    )
                }
        }

    }
}