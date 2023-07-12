package io.hamal.app.proxy.config

import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.ReceiptRepository
import io.hamal.app.proxy.repository.SqliteBlockRepository
import io.hamal.app.proxy.repository.SqliteReceiptRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
@OptIn(ExperimentalSerializationApi::class)
class RepositoryConfig {
    @Bean
    fun protobuf() = ProtoBuf {}

    @Bean
    fun blockRepository(
        protoBuf: ProtoBuf
    ): BlockRepository = SqliteBlockRepository(path, protoBuf)

    @Bean
    fun receiptRepository(
        protoBuf: ProtoBuf
    ): ReceiptRepository = SqliteReceiptRepository(path, protoBuf)

    private val path = Path("/tmp/hamal/proxy")
}