package io.hamal.app.web3proxy

import io.hamal.app.web3proxy.arbitrum.fixture.ArbitrumBatchServiceFixture
import io.hamal.app.web3proxy.arbitrum.handler.ArbitrumRequestHandlerImpl
import io.hamal.app.web3proxy.arbitrum.handler.HandleArbitrumRequest
import io.hamal.app.web3proxy.arbitrum.repository.ArbitrumRepositoryImpl
import io.hamal.app.web3proxy.eth.fixture.EthBatchServiceFixture
import io.hamal.app.web3proxy.eth.handler.EthRequestHandlerImpl
import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.app.web3proxy.eth.repository.EthRepositoryImpl
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import kotlin.io.path.createTempDirectory

@TestConfiguration
class TestConfig {

    @Bean
    fun arbitrumRequestHandler(): HandleArbitrumRequest = ArbitrumRequestHandlerImpl(
        ArbitrumRepositoryImpl(
            path = createTempDirectory("web3proxy_arbitrum"),
            batchService = ArbitrumBatchServiceFixture()
        )
    )

    @Bean
    fun ethRequestHandler(): HandleEthRequest = EthRequestHandlerImpl(
        EthRepositoryImpl(
            path = createTempDirectory("web3proxy_eth"),
            batchService = EthBatchServiceFixture()
        )
    )

}