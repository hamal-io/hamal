package com.nyanbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc


@EnableWebMvc
@Configuration
@SpringBootApplication
class NyanBot

fun main(args: Array<String>) {
    SpringApplication(NyanBot::class.java).run(*args)
}
