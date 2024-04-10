package com.nyanbot

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc


@EnableWebMvc
@Configuration
@SpringBootApplication
class NyanBot {

    @Bean
    fun commandLineRunner(
        @Value("\${io.hamal.server.url}") url: String,
        @Value("\${io.hamal.server.token}") token: String
    ): CommandLineRunner {
        return CommandLineRunner {
            val sdk = ApiSdkImpl(HttpTemplateImpl(
                baseUrl = url,
                headerFactory = { this["Authorization"] = "Bearer $token" }
            ))

            val me = sdk.account.me()
            val workspaceId = me.workspaces.first().id

            sdk.namespace.list(workspaceId)

            println("Called")
        }
    }

}

fun main(args: Array<String>) {
    SpringApplication(NyanBot::class.java).run(*args)
}
