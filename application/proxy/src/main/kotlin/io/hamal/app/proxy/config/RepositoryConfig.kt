package io.hamal.app.proxy.config

import io.hamal.app.proxy.repository.*
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
    ): BlockRepository = SqliteBlockRepository(path)

//    @Bean
//    fun receiptRepository(
//        protoBuf: ProtoBuf
//    ): ReceiptRepository = SqliteReceiptRepository(path, protoBuf)

    @Bean
    fun proxyRepository(
        blockRepository: BlockRepository
    ): ProxyRepository = SqliteProxyRepository(
        addressRepository = AddressRepository(path),
        blockRepository = blockRepository,
        transactionRepository = SqliteTransactionRepository(path)
    )

//    @Bean
//    fun commandLineRunner(): CommandLineRunner {
//        return object : CommandLineRunner {
//            @Autowired
//            lateinit var proxyRepository: ProxyRepository
//
//            override fun run(vararg args: String?) {
////                val start = 17771431
//                val start = 17776882
//                val end = 17781431
////                for (x in 17771431..17781431) {
//
//
//                val stepSize = 2
//
//                var current = start
//
//                while (true) {
//                    try {
//
//                        if (current >= end) {
//                            break
//                        }
//
//                        val srv = EthHttpBatchService(HttpTemplate("http://localhost:8081"))
//                        for (x in 0..stepSize) {
//                            srv.getBlock(EthUint64((current + x).toLong()))
////                            current += x
//                        }
//
//                        val start = System.currentTimeMillis()
//                        val blocks = srv.execute().map { it as EthGetBlockResp }
//
//                        blocks.forEach { resp ->
//                            val block = resp.result
//
//                            proxyRepository.store(block)
//
//                            println(block.number)
//                        }
//
//                        current += stepSize
//
//                    } catch (t: Throwable) {
//                        t.printStackTrace()
//
//                    }
//                }
//            }
//        }
//    }

    private val path = Path("/tmp/hamal/db")

}