package io.hamal.app.proxy.config

import io.hamal.app.proxy.cache.Cache
import io.hamal.app.proxy.repository.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
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
    ): DepBlockRepository = DepSqliteBlockRepository(path, protoBuf)

    @Bean
    fun receiptRepository(
        protoBuf: ProtoBuf
    ): ReceiptRepository = SqliteReceiptRepository(path, protoBuf)

    @Bean
    fun proxyRepository(): ProxyRepository = SqliteProxyRepository(
        addressRepository = AddressRepository(),
        blockRepository = SqliteBlockRepository(path),
        hashRepository = HashRepository(),
        transactionRepository = SqliteTransactionRepository(path)
    )

    @Bean
    fun commandLineRunner(): CommandLineRunner {
        return object : CommandLineRunner {
            @Autowired
            lateinit var cache: Cache

            @Autowired
            lateinit var proxyRepository: ProxyRepository

            override fun run(vararg args: String?) {
//                val start = 17771431
                val start = 17776882
                val end = 17781431
//                for (x in 17771431..17781431) {


                val stepSize = 50

                var current = start

                while (true) {
                    try {

                        if (current >= end) {
                            break
                        }

                        val srv = EthHttpBatchService(HttpTemplate("http://localhost:8081"))
                        for (x in 0..stepSize) {
                            srv.getBlock(EthUint64((current + x).toLong()))
//                            current += x
                        }

                        val start = System.currentTimeMillis()
                        val blocks = srv.execute().map { it as EthGetBlockResp }

                        blocks.forEach { resp ->
                            val block = resp.result

                            proxyRepository.store(block)

                            println(block.number)
                        }

                        current += stepSize

                    } catch (t: Throwable) {
                        t.printStackTrace()

                    }
                }
            }
        }
    }

    private val path = Path("/tmp/hamal/db")

}