package io.hamal.app.web3proxy.eth.handler

import io.hamal.app.web3proxy.eth.repository.EthRepository
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.impl.eth.domain.EvmRequest
import io.hamal.lib.web3.evm.impl.eth.domain.EvmResponse

interface HandleEthRequest {
    operator fun invoke(request: EvmRequest): EvmResponse
    operator fun invoke(requests: List<EvmRequest>): List<EvmResponse>
}

class EthRequestHandlerImpl(
    private val ethRepository: EthRepository,
) : HandleEthRequest {

    override fun invoke(request: EvmRequest): EvmResponse {
        return invoke(listOf(request)).firstOrNull()
            ?: throw RuntimeException("Unable to process request")
    }

    override fun invoke(requests: List<EvmRequest>): List<EvmResponse> {
        return requests.filterIsInstance<EthGetBlockByNumberRequest>().let { reqs ->
            ethRepository.listBlocks(reqs.map { it.number })
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