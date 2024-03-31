package io.hamal.app.web3proxy.config

import io.hamal.app.web3proxy.arbitrum.handler.ArbitrumRequestHandlerImpl
import io.hamal.app.web3proxy.arbitrum.handler.HandleArbitrumRequest
import io.hamal.app.web3proxy.arbitrum.repository.ArbitrumRepositoryImpl
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.evm.chain.arbitrum.http.ArbitrumHttpBatchService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.io.path.Path

@Configuration
@Profile("!test")
class ArbitrumConfig {

    @Bean
    fun arbitrumRequestHandler(
        @Value("\${io.hamal.web3proxy.arbitrum.upstream}") upstreamHost: String
    ): HandleArbitrumRequest = ArbitrumRequestHandlerImpl(
        ArbitrumRepositoryImpl(
            path = Path("/opt/web3proxy/arbitrum"),
            batchService = ArbitrumHttpBatchService(HttpTemplateImpl(upstreamHost))
        )
    )

}