package io.hamal.app.web3proxy.component

import io.hamal.app.web3proxy.repository.BlockRepository
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.web3.eth.domain.EthMethod
import io.hamal.lib.web3.eth.domain.EthRequestId
import io.hamal.lib.web3.eth.domain.EthResponse

interface RequestHandler {

    fun handle(requests: List<Request>): List<EthResponse>

    data class Request(
        val id: EthRequestId,
        val method: EthMethod,
        val params: HotArray,
    )
}

class RequestHandlerImpl(
    private val blockRepository: BlockRepository
) : RequestHandler {

    override fun handle(requests: List<RequestHandler.Request>): List<EthResponse> {
        TODO("Not yet implemented")
    }
}