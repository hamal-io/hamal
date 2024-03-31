package io.hamal.app.web3proxy.arbitrum

import io.hamal.app.web3proxy.TestConfig
import io.hamal.app.web3proxy.Web3Proxy
import io.hamal.app.web3proxy.config.WebConfig
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.JsonHttpSerdeFactory
import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumTransactionData
import io.hamal.lib.web3.json
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [Web3Proxy::class, WebConfig::class, TestConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
internal abstract class ArbitrumBaseTest {

    protected fun assertBlock0x100002(block: ArbitrumBlockData?) {
        checkNotNull(block)

        assertThat(block.extraData, equalTo(EvmBytes32("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.gasLimit, equalTo(EvmUint64("0x34c8134")))
        assertThat(block.gasUsed, equalTo(EvmUint64("0x365b2")))
        assertThat(block.hash, equalTo(EvmHash("0xbe1ba35d8f2b0911fe5874bc5fcaab85b75cf121468872935f6ec7b62075c7b6")))
        assertThat(block.l1BlockNumber, equalTo(EvmUint64("0xc9f91e")))
        assertThat(
            block.logsBloom,
            equalTo(EvmPrefixedHexString("0x00000002000000000000100000000000000000000000000000000000000000100000000000000000000000000000000000004000000020000000000000000000000000010000000800000008000000000000000000000000000000000000000000000000020000000000100400000800000000000000000000000010000800000800000000000000000001000000000000000000000000000000002000000000000000000001000000000000800000000000000000000000000000000000000800000002000000000000000000000000008000000000208000000000000020000000000000000000000800000000000000000000000000000000000000000080"))
        )
        assertThat(block.miner, equalTo(EvmAddress("0x0000000000000000000000000000000000000000")))
        assertThat(block.mixHash, equalTo(EvmHash("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.number, equalTo(EvmUint64("0x100002")))
        assertThat(block.parentHash, equalTo(EvmHash("0xdb1a732e6f909abb5f18b529c0eb5776217c08dc4fd7a966edeefa9547321fc9")))
        assertThat(block.receiptsRoot, equalTo(EvmHash("0xc0c9dc2e54ec56b519d0de215b52d71bf2b02ebaee736250babb6e0c35303c86")))
        assertThat(block.sha3Uncles, equalTo(EvmHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.size, equalTo(EvmUint64("0x3ae")))
        assertThat(block.stateRoot, equalTo(EvmHash("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.timestamp, equalTo(EvmUint64("0x61432b9b")))
        assertThat(block.totalDifficulty, equalTo(EvmUint256("0x0")))
        assertThat(block.transactions, hasSize(1))

        assertTransaction0x47dcb139b6360035b65bc822b519fe95a4ea16e33786144fc4d4c6fb65075f3f(block.transactions[0])

    }

    protected fun assertBlock0x1284810(block: ArbitrumBlockData?) {
        checkNotNull(block)

        assertThat(block.extraData, equalTo(EvmBytes32("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.gasLimit, equalTo(EvmUint64("0xbd4c9b7")))
        assertThat(block.gasUsed, equalTo(EvmUint64("0x2753a")))
        assertThat(block.hash, equalTo(EvmHash("0x5c3248cf43fb3bd608d97fe3b2727bd8669364cd4f4432bd93abcb5fd0f9e616")))
        assertThat(block.l1BlockNumber, equalTo(EvmUint64("0xe926fb")))
        assertThat(
            block.logsBloom,
            equalTo(EvmPrefixedHexString("0x00000000040000000400000080000040000000000000000010000000040000000000000000000000000000000000000800000000000000000000000800200000000010000000000000000008000000000000000000000000000000000000000000000000628080000000000000000800000000000000000000000010000000000000000000000000000000000020000000002000000000000000000000400000020000000000000080000000000000000000020000000000004000000000000000008042000108000000000000000000000000008000000000000000000020000010000000000000000000000000000010000000000004000420040400000000"))
        )
        assertThat(block.miner, equalTo(EvmAddress("0x0000000000000000000000000000000000000000")))
        assertThat(block.mixHash, equalTo(EvmHash("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.number, equalTo(EvmUint64("0x1284810")))
        assertThat(block.parentHash, equalTo(EvmHash("0xf3b5038125fa43cf43ca8b3919a7b7f272dff043964b3a4bcf239e28ff38cff0")))
        assertThat(block.receiptsRoot, equalTo(EvmHash("0xd548d9a365a61bc94e659074d0f5b659bd7aa2d1a307630a23f0c8edbbcbd76c")))
        assertThat(block.sha3Uncles, equalTo(EvmHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.size, equalTo(EvmUint64("0x3e9")))
        assertThat(block.stateRoot, equalTo(EvmHash("0x0000000000000000000000000000000000000000000000000000000000000000")))
        assertThat(block.timestamp, equalTo(EvmUint64("0x62ec8ce0")))
        assertThat(block.totalDifficulty, equalTo(EvmUint256("0x0")))
        assertThat(block.transactions, hasSize(1))

    }

    protected fun assertTransaction0x47dcb139b6360035b65bc822b519fe95a4ea16e33786144fc4d4c6fb65075f3f(tx: ArbitrumTransactionData) {
        assertThat(tx.blockHash, equalTo(EvmHash("0xbe1ba35d8f2b0911fe5874bc5fcaab85b75cf121468872935f6ec7b62075c7b6")))
        assertThat(tx.blockNumber, equalTo(EvmUint64("0x100002")))
        assertThat(tx.from, equalTo(EvmAddress("0x72b4da7114a424aa2d095f1276087908b7f212a8")))
        assertThat(tx.gas, equalTo(EvmUint64("0x16952c")))
        assertThat(tx.gasPrice, equalTo(EvmUint64("0x4cdffa42")))
        assertThat(tx.hash, equalTo(EvmHash("0x47dcb139b6360035b65bc822b519fe95a4ea16e33786144fc4d4c6fb65075f3f")))
        assertThat(
            tx.input,
            equalTo(EvmPrefixedHexString("0x414bf38900000000000000000000000082af49447d8a07e3bd95bd0d56f35241523fbab1000000000000000000000000ff970a61a04b1ca14834a43f5de4533ebddb5cc80000000000000000000000000000000000000000000000000000000000000bb800000000000000000000000072b4da7114a424aa2d095f1276087908b7f212a80000000000000000000000000000000000000000000000000000000061432e3a00000000000000000000000000000000000000000000000001445a652d63aac90000000000000000000000000000000000000000000000000000000012f697fc0000000000000000000000000000000000000000000000000000000000000000"))
        )
        assertThat(tx.nonce, equalTo(EvmUint64("0x1")))
        assertThat(tx.to, equalTo(EvmAddress("0xe592427a0aece92de3edee1f18e0157c05861564")))
        assertThat(tx.transactionIndex, equalTo(EvmUint32("0x0")))
        assertThat(tx.value, equalTo(EvmUint256("0x1445a652d63aac9")))
        assertThat(tx.type, equalTo(EvmUint8("0x78")))
    }


    @LocalServerPort
    lateinit var localPort: Number

    protected val testTemplate by lazy {
        HttpTemplateImpl(baseUrl = "", serdeFactory = JsonHttpSerdeFactory(json)).post("http://localhost:${localPort}/arbitrum")
    }
}