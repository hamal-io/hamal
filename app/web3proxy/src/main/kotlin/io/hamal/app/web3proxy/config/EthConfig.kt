package io.hamal.app.web3proxy.config

import io.hamal.app.web3proxy.eth.handler.EthRequestHandlerImpl
import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.app.web3proxy.eth.repository.EthRepositoryImpl
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path

@Configuration
@Profile("!test")
class EthConfig {

    @Bean
    fun ethRequestHandler(
        @Value("\${io.hamal.web3proxy.eth.upstream}") upstreamHost: String
    ): HandleEthRequest = EthRequestHandlerImpl(
        EthRepositoryImpl(
            path = Path("/tmp/web3proxy/eth"),
            ethBatchService = EthHttpBatchService(HttpTemplateImpl(upstreamHost))
        )
    )

}