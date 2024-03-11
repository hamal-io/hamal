package io.hamal.app.web3proxy.config

import io.hamal.app.web3proxy.component.RequestHandler
import io.hamal.app.web3proxy.component.RequestHandlerImpl
import io.hamal.app.web3proxy.repository.eth.EthBlockRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EthConfig {

    @Bean
    fun ethRequestHandler(): RequestHandler =
        RequestHandlerImpl(
            EthBlockRepository()
        )

}