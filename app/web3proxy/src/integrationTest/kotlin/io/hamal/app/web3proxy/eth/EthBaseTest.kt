package io.hamal.app.web3proxy.eth

import io.hamal.app.web3proxy.Web3Proxy
import io.hamal.app.web3proxy.config.WebConfig
import io.hamal.app.web3proxy.eth.handler.EthRequestHandlerImpl
import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.app.web3proxy.eth.repository.EthRepositoryImpl
import io.hamal.app.web3proxy.fixture.EthBatchServiceFixture
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.JsonHttpSerdeFactory
import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.impl.eth.domain.EthBlock
import io.hamal.lib.web3.evm.impl.eth.domain.EthTransaction
import io.hamal.lib.web3.json
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.io.path.createTempDirectory


@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [Web3Proxy::class, WebConfig::class, TestConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
internal abstract class EthBaseTest {

    protected fun assertBlock0x100002(block: EthBlock?) {
        checkNotNull(block)

        assertThat(block.extraData, equalTo(EvmBytes32("0xd783010303844765746887676f312e352e31856c696e7578")))
        assertThat(block.gasLimit, equalTo(EvmUint64("0x2fefd8")))
        assertThat(block.gasUsed, equalTo(EvmUint64("0xf618")))
        assertThat(block.hash, equalTo(EvmHash("0x54dd95b568a08a6bcc3ad1675810b8ec841ef5eed7ac027cce35bb2a097671f9")))
        assertThat(
            block.logsBloom,
            equalTo(EvmPrefixedHexString("0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"))
        )
        assertThat(block.miner, equalTo(EvmAddress("0x2a65aca4d5fc5b5c859090a6c34d164135398226")))
        assertThat(block.mixHash, equalTo(EvmHash("0x4e30728efe54a4f4dcee2760f8691058abf340e91f4cd2b9f86ca17cc4f32574")))
        assertThat(block.number, equalTo(EvmUint64("0x100002")))
        assertThat(block.parentHash, equalTo(EvmHash("0x18c68d9ba58772a4409d65d61891b25db03a105a7769ae08ef2cff697921b446")))
        assertThat(block.receiptsRoot, equalTo(EvmHash("0x72bfd2b2b986dfbb904161426f2f9ab419e219174e41ab68baef84ecf64b2630")))
        assertThat(block.sha3Uncles, equalTo(EvmHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.size, equalTo(EvmUint64("0x36f")))
        assertThat(block.stateRoot, equalTo(EvmHash("0xb3b2f5293097b39ad903df8ba0a17c04832b2d6a7fff3c3c3dd6afe2dbfa96d7")))
        assertThat(block.timestamp, equalTo(EvmUint64("0x56cc7ba8")))
        assertThat(block.totalDifficulty, equalTo(EvmUint256("0x6babaca3ae996bed")))
        assertThat(block.transactions, hasSize(3))

        assertTransaction0x7da0e4de6f6f08c3e103d8a9fbb8906963b2547af0431d28aec866f7bbbdec5d(block.transactions[1])

        assertThat(block.withdrawals, nullValue())
        assertThat(block.withdrawalsRoot, nullValue())
    }

    protected fun assertBlock0x1284810(block: EthBlock?) {
        checkNotNull(block)

        assertThat(block.extraData, equalTo(EvmBytes32("0x546974616e2028746974616e6275696c6465722e78797a29")))
        assertThat(block.gasLimit, equalTo(EvmUint64("0x1c9c380")))
        assertThat(block.gasUsed, equalTo(EvmUint64("0xa0adf6")))
        assertThat(block.hash, equalTo(EvmHash("0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")))
        assertThat(
            block.logsBloom,
            equalTo(EvmPrefixedHexString("0x40b30d48e16500351a2210288d8a9a2310d82f744f40041100995012d02a434245553590c42a6c61ebb0384248132177e3259218ba4269f0f6f52d9052a8ec29e87247394a228c3fbd02f76f942304f59d2641c1517589284e813d42a0e61b48981d042216061e5c101cdac482625c65f2776c682e105721c64dc09218eb10057c36b2da2a1443c8004f325a622209c554724d97fb4b30eb7436304ae25226238b0456923796a2905f04f4d4b9da049815518640e4acba0fae7d348c1a0094f4ed57302244b50004a82888c240ad3ace7d7200af942b0052e4573bba1054a844b172a0041c49962a510425d7a440a8c3dcf9b4080a7e064008bd281070937c0d"))
        )
        assertThat(block.miner, equalTo(EvmAddress("0x4838b106fce9647bdf1e7877bf73ce8b0bad5f97")))
        assertThat(block.mixHash, equalTo(EvmHash("0x312b2e833821b694a1789d9efc7618a3a600cc06c7e4ed4757860c998e69081b")))
        assertThat(block.number, equalTo(EvmUint64("0x1284810")))
        assertThat(block.parentHash, equalTo(EvmHash("0x3da1516115fd35fbe3c4d83f7422bf7d5f973b08f09028ea18ae27346181da52")))
        assertThat(block.receiptsRoot, equalTo(EvmHash("0x1f47a6666ca0f3038ec6e0fc236f58b2aad8933efc24e7f9c28d66c39cc2a45e")))
        assertThat(block.sha3Uncles, equalTo(EvmHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.size, equalTo(EvmUint64("0xfbec")))
        assertThat(block.sha3Uncles, equalTo(EvmHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.stateRoot, equalTo(EvmHash("0xfaca9e1209e82e72e5700512d0cfedcb156baca691b1cc56e80ea72e4d26dbaf")))
        assertThat(block.timestamp, equalTo(EvmUint64("0x65eff103")))
        assertThat(block.totalDifficulty, equalTo(EvmUint256("0xc70d815d562d3cfa955")))
        assertThat(block.transactions, hasSize(3))

        assertTransaction0x93508d92b49b73aea5b8394a0394dac83abd81f9dba4424b2604e43108245b33(block.transactions[1])

        assertThat(block.withdrawals, hasSize(2))
        assertThat(block.withdrawalsRoot, equalTo(EvmHash("0x79373dfb0ff17072206ea0dae4942afa3791a9699aeacdb74db8a4f8b62b374f")))
    }

    protected fun assertTransaction0x7da0e4de6f6f08c3e103d8a9fbb8906963b2547af0431d28aec866f7bbbdec5d(tx: EthTransaction) {
        assertThat(tx.blockHash, equalTo(EvmHash("0x54dd95b568a08a6bcc3ad1675810b8ec841ef5eed7ac027cce35bb2a097671f9")))
        assertThat(tx.blockNumber, equalTo(EvmUint64("0x100002")))
        assertThat(tx.from, equalTo(EvmAddress("0x33357e443d5f03882af49bdcc62ca364ee0a6c7b")))
        assertThat(tx.gas, equalTo(EvmUint64("0x5208")))
        assertThat(tx.gasPrice, equalTo(EvmUint64("0xba43b7400")))
        assertThat(tx.maxPriorityFeePerGas, nullValue())
        assertThat(tx.maxFeePerGas, nullValue())
        assertThat(tx.hash, equalTo(EvmHash("0x7da0e4de6f6f08c3e103d8a9fbb8906963b2547af0431d28aec866f7bbbdec5d")))
        assertThat(tx.input, equalTo(EvmPrefixedHexString("0x")))
        assertThat(tx.nonce, equalTo(EvmUint64("0xe")))
        assertThat(tx.to, equalTo(EvmAddress("0x181A54307dD7075a6D628014a59c1E48D1D4d2A0")))
        assertThat(tx.transactionIndex, equalTo(EvmUint32("0x1")))
        assertThat(tx.value, equalTo(EvmUint256("0xdaf134736c65000")))
        assertThat(tx.type, equalTo(EvmUint8("0x0")))
        assertThat(tx.accessList, nullValue())
    }

    protected fun assertTransaction0x93508d92b49b73aea5b8394a0394dac83abd81f9dba4424b2604e43108245b33(tx: EthTransaction) {
        assertThat(tx.blockHash, equalTo(EvmHash("0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")))
        assertThat(tx.blockNumber, equalTo(EvmUint64("0x1284810")))
        assertThat(tx.from, equalTo(EvmAddress("0x7088659e96e5dcc91608d871937bdb5fa0776bac")))
        assertThat(tx.gas, equalTo(EvmUint64("0xdbba0")))
        assertThat(tx.gasPrice, equalTo(EvmUint64("0xcdeac4a95")))
        assertThat(tx.maxPriorityFeePerGas, equalTo(EvmUint64("0x1")))
        assertThat(tx.maxFeePerGas, equalTo(EvmUint64("0xf719b8cb1")))
        assertThat(tx.hash, equalTo(EvmHash("0x93508d92b49b73aea5b8394a0394dac83abd81f9dba4424b2604e43108245b33")))
        assertThat(
            tx.input,
            equalTo(
                EvmPrefixedHexString(
                    "0xe16dd1ef0000000000000000000000000000000000000000000000000000000000000080000000000000000000000000b3f7b16349364589d1615f2ca9f29f67a1878e70000000000000000000000000000000000000000000013e2d09ff7efe0f392603000000000000000000000000000000000000000000000000005d77f46b83db200000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000c7e66cb8ff0e00000000000000000000000000000000000000000000000dc86b8172f313049bf270000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000028c02aaa39b223fe8d0a0e5c4f27ead9083c756cc21c9922314ed1415c95b9fd453c3818fd41867d0b000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000058d8305238780000000000000000000000000000000000000000000000061a651e84fccdeef66dc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000028c02aaa39b223fe8d0a0e5c4f27ead9083c756cc21c9922314ed1415c95b9fd453c3818fd41867d0b000000000000000000000000000000000000000000000000"
                )
            )
        )
        assertThat(tx.nonce, equalTo(EvmUint64("0x3288")))
        assertThat(tx.to, equalTo(EvmAddress("0xc6fecdf760af24095cded954de7d81ab49f8bae1")))
        assertThat(tx.transactionIndex, equalTo(EvmUint32("0x1")))
        assertThat(tx.value, equalTo(EvmUint256("0x0")))
        assertThat(tx.type, equalTo(EvmUint8("0x2")))
        assertThat(tx.accessList, empty())
    }

    @LocalServerPort
    lateinit var localPort: Number

    protected val testTemplate by lazy {
        HttpTemplateImpl(baseUrl = "", serdeFactory = JsonHttpSerdeFactory(json)).post("http://localhost:${localPort}/eth")
    }
}

@TestConfiguration
class TestConfig {

    @Bean
    fun ethRequestHandler(): HandleEthRequest = EthRequestHandlerImpl(
        EthRepositoryImpl(
            path = createTempDirectory("web3proxy_eth"),
            ethBatchService = EthBatchServiceFixture()
        )
    )

}