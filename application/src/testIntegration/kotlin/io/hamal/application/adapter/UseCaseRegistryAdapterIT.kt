package io.hamal.application.adapter


import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseHandler
import io.hamal.lib.ddd.usecase.GetCommandUseCaseHandlerPort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootTest
class TestApplication

@Configuration
open class TestUseCaseRegistryConfig {
    @Bean
    open fun getCommandUseCaseHandlerPort(): GetCommandUseCaseHandlerPort = DefaultUseCaseRegistryAdapter()
}

@Configuration
open class TestUseCasesConfig {

    class TestCommandUseCase : CommandUseCase

    @Bean
    open fun commandUseCaseHandler(): CommandUseCaseHandler<String, TestCommandUseCase> {
        return object : CommandUseCaseHandler.BaseImpl<String, TestCommandUseCase>(
            String::class, TestCommandUseCase::class
        ) {
            override fun handle(useCase: TestCommandUseCase): List<String> {
                TODO("Not yet implemented")
            }
        }
    }

}

@SpringBootTest(
    classes = [TestApplication::class, TestUseCaseRegistryConfig::class, TestUseCasesConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UseCaseRegistryAdapterIT {

//    @Test
//    fun `fails`() {
//        throw Error("xzy")
//    }
    @Autowired
    var testInstance: DefaultUseCaseRegistryAdapter? = null

    @Test
    fun `ok`() {
        val x = testInstance?.get(String::class, TestUseCasesConfig.TestCommandUseCase::class)
        println(x)
    }

}