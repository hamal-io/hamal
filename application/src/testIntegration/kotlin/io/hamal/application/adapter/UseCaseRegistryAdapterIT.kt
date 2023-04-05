package io.hamal.application.adapter


import io.hamal.lib.ddd.usecase.CommandUseCasePayload
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.GetCommandUseCasePort
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
    open fun getCommandUseCasePort(): GetCommandUseCasePort = DefaultUseCaseRegistryAdapter()
}

@Configuration
open class TestUseCasesConfig {

    class TestCommandUseCasePayload : CommandUseCasePayload

    @Bean
    open fun commandUseCase(): CommandUseCase<String, TestCommandUseCasePayload> {
        return object : CommandUseCase.BaseImpl<String, TestCommandUseCasePayload>(
            String::class, TestCommandUseCasePayload()
        ) {
            override operator fun invoke(payload: TestCommandUseCasePayload): List<String> {
                return emptyList()
            }
        }
    }

}

@SpringBootTest(
    classes = [TestApplication::class, TestUseCaseRegistryConfig::class, TestUseCasesConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UseCasePayloadRegistryAdapterIT {

//    @Test
//    fun `fails`() {
//        throw Error("xzy")
//    }
    @Autowired
    var testInstance: DefaultUseCaseRegistryAdapter? = null

    @Test
    fun `ok`() {
        val x = testInstance?.get(String::class, TestUseCasesConfig.TestCommandUseCasePayload::class)
        println(x)
    }

}