package io.hamal.app.web3proxy.arbitrum.repository

import io.hamal.app.web3proxy.arbitrum.fixture.ArbitrumBatchServiceFixture
import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.createTempDirectory

internal class ListBlocksBySerdeNumberTest {

    @Test
    fun `Does nothing on empty block number list`() {
        testInstance.listBlocks(listOf()).also { result ->
            assertThat(result, hasSize(0))
        }

        verifyNoDownloadAttempts()
    }

    @Test
    fun `Loads block from upstream if it does not exist yet`() {
        testInstance.list("0x1284810").also { result ->
            assertThat(result, hasSize(1))
            assertBlock0x1284810(result[0])
        }

        verifyOneBlockDownloadAttempt()
    }

    @Test
    fun `Loads block from database if already download`() {
        repeat(10) {
            testInstance.list("0x1284810")
            verifyOneBlockDownloadAttempt()
        }

        resetBatchService()

        testInstance.list("0x1284810").also { result ->
            assertThat(result, hasSize(1))
            assertBlock0x1284810(result[0])
        }

        verifyNoDownloadAttempts()
    }

    @Test
    fun `Loads block existing block from database and only download non existent one`() {
        testInstance.list("0x1284810")
        resetBatchService()

        testInstance.list("0x1284810", "0x1284811").also { result ->
            assertThat(result, hasSize(2))
            assertBlock0x1284810(result[0])
            assertBlock0x1284811(result[1])
        }

        verifyOneBlockDownloadAttempt()
    }

    @Test
    fun `Blocks are returned ordered by block number list`() {
        testInstance.list("0x1284810", "0x1284811").also { result ->
            assertThat(result, hasSize(2))
            assertBlock0x1284810(result[0])
            assertBlock0x1284811(result[1])
        }
        verifyTwoBlocksDownloadAttempts()

        resetBatchService()
        testInstance.list("0x1284811", "0x1284810").also { result ->
            assertThat(result, hasSize(2))
            assertBlock0x1284811(result[0])
            assertBlock0x1284810(result[1])
        }
    }

    @Test
    fun `Future blocks will be loaded as null`() {
        testInstance.list("0xB4DC0D3", "0xBADC0FFEEBABE").also { result ->
            assertThat(result, hasSize(2))
            assertThat(result[0], nullValue())
            assertThat(result[1], nullValue())
        }
        verifyTwoBlocksDownloadAttempts()
    }

    @Test
    fun `Can mix existing and future blocks`() {
        testInstance.list("0x1284811", "0xB4DC0D3", "0xBADC0FFEEBABE", "0x1284810").also { result ->
            assertThat(result, hasSize(4))
            assertBlock0x1284811(result[0])
            assertThat(result[1], nullValue())
            assertThat(result[2], nullValue())
            assertBlock0x1284810(result[3])
        }

        verifyFourBlockDownloadAttempts()
    }

    @BeforeEach
    fun setup() {
        testInstance.clear()
    }

    private fun resetBatchService() {
        batchServiceFixture.clear()
    }

    private fun assertBlock0x1284810(block: ArbitrumBlockData?) {
        checkNotNull(block)
        assertThat(block.extraData, equalTo(EvmBytes32("0x0000000000000000000000000000000000000000000000000000000000000000")))
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
        assertThat(block.gasLimit, equalTo(EvmUint64("0xbd4c9b7")))
        assertThat(block.gasUsed, equalTo(EvmUint64("0x2753a")))
        assertThat(block.totalDifficulty, equalTo(EvmUint256("0x0")))
        assertThat(block.transactions, hasSize(1))

        block.transactions[0].also { tx ->
            assertThat(tx.blockHash, equalTo(EvmHash("0x5c3248cf43fb3bd608d97fe3b2727bd8669364cd4f4432bd93abcb5fd0f9e616")))
            assertThat(tx.blockNumber, equalTo(EvmUint64("0x1284810")))
            assertThat(tx.from, equalTo(EvmAddress("0x230a1ac45690b9ae1176389434610b9526d2f21b")))
            assertThat(tx.gas, equalTo(EvmUint64("0x4c4b40")))
            assertThat(tx.gasPrice, equalTo(EvmUint64("0x9766be2")))
            assertThat(tx.hash, equalTo(EvmHash("0x56f56a8352784709830b1414166e5c2fa657ef05795ab1a7ff241e1fa68a4cc0")))
            assertThat(
                tx.input,
                equalTo(
                    EvmPrefixedHexString(
                        "0x17357892000000000000000000000000a665a0507ad4b0571b12b1f59fa7e8d2bf63c65f0000000000000000000000002913e812cf0dcca30fb28e6cac3d2dcff4497688000000000000000000000000000000000000000000000144cdacdb33889da2bb0000000000000000000000000000000000000000000000006124fee993bc00000000000000000000000000009dd329f5411466d9e0c488ff72519ca9fef0cb40000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000156e9ec2e0000000000000000000000000000000000000000000000000000000062edde8f197734761438890268958a2cf76982d2365f98f80371e03b97c1f9423fa8b98d"
                    )
                )
            )
            assertThat(tx.nonce, equalTo(EvmUint64("0xd159")))
            assertThat(tx.to, equalTo(EvmAddress("0x6f4e8eba4d337f874ab57478acc2cb5bacdc19c9")))
            assertThat(tx.transactionIndex, equalTo(EvmUint32("0x0")))
            assertThat(tx.value, equalTo(EvmUint256("0x0")))
            assertThat(tx.type, equalTo(EvmUint8("0x78")))
        }
    }

    private fun assertBlock0x1284811(block: ArbitrumBlockData?) {
        checkNotNull(block)

        assertThat(block.number, equalTo(EvmUint64("0x1284811")))
        assertThat(block.transactions, hasSize(1))

        block.transactions[0].also { tx ->
            assertThat(tx.blockHash, equalTo(EvmHash("0x655185e862a41eb2567d7690b9f132c39d2dac9b95b7a4a8d856754f692d5093")))
            assertThat(tx.blockNumber, equalTo(EvmUint64("0x1284811")))
            assertThat(tx.from, equalTo(EvmAddress("0x160694f252b907cdf3862922950142a5f1fd161a")))
            assertThat(tx.gas, equalTo(EvmUint64("0x4c4b41")))
            assertThat(tx.gasPrice, equalTo(EvmUint64("0x9ba56fb")))
            assertThat(tx.hash, equalTo(EvmHash("0x9fe1f58ad95df1905ea459ec5feb41971b4be84051d83493a046c1357b63f064")))
            assertThat(
                tx.input,
                equalTo(EvmPrefixedHexString("0xb87b0b4c0000000000000000000000000ae392879a228b2484d9b1f80a5d0b7080fe79c20000000000000000000000000000000000000000000000000000000000000060000000000000000000000000fd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb9000000000000000000000000000000000000000000000000000000000000088422045fbe000000000000000000000000cf4d2994088a8cde52fb584fe29608b63ec063b200000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000fd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb95a2fe19b84b6f118465ac699e68b8532868ffd6ee80f036cd39392cf130d1da400000000000000000000000000000000000000000000000000000000000007a49b151a800000000000000000000000000000000000000000000000000000000000000020000000000000000000000000cf4d2994088a8cde52fb584fe29608b63ec063b2000000000000000000000000997f29174a766a1da04cf77d135d59dd12fb54d1000000000000000000000000e026086181bcfbd06db4c67739aa9c36054d5551000000000000000000000000362fa9d0bca5d19f743db50738345ce2b40ec99f000000000000000000000000c2132d05d31c914a87c6611c10748aeb04b58e8f000000000000000000000000fd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb900000000000000000000000038d1fb9eae876eeddab633aead79c1e3e50b38a300000000000000000000000038d1fb9eae876eeddab633aead79c1e3e50b38a3000000000000000000000000362fa9d0bca5d19f743db50738345ce2b40ec99f433d84b5402454311913f223ccb89957759930f6e0fe77e9538b996dcc50e82676266281053e39d67b226d4dbfb3fb3af4ae032a6603f603eea3b8d2ca1b38d70000000000000000000000000000000000000000000000000000000000000089000000000000000000000000000000000000000000000000000000000000a4b100000000000000000000000000000000000000000000000000000000009844620000000000000000000000000000000000000000000000000000000062ef2f850000000000000000000000000000000000000000000000000000000000e926fb0000000000000000000000000000000000000000000000000000000000052c000000000000000000000000000000000000000000000000000000000000000280000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000007600000000000000000000000000000000000000000000000000000000000000041df67d5dca68cd5112cd4255fc1000015dda85ceedf5b75f0639ddeee65cd80275052146b15918bc769b9b24f721bdbd21be272d54207643b5cab376acd7c6d2f1c000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000424cf76d313000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000001c0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000038d1fb9eae876eeddab633aead79c1e3e50b38a315e1e317867749f0c65345db746d6a531dd0dad133197553433a866cfa5abada000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002791bca1f2de4661ed88a30c99a7a9449aa84174000000000000000000000000000000000000000000000000000000000000000000000000000000000000000038d1fb9eae876eeddab633aead79c1e3e50b38a3000000000000000000000000000000000000000000000000000000000000a4b10000000000000000000000000000000000000000000000000000000000989680000000000000000000000000000000000000000000000000000000000000000e7472616e73666572746f2e78797a0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000002000000000000000000000000088cbf433471a0cd8240d2a12354362988b4593e5000000000000000000000000a867241cdc8d3b0c07c85cc06f25a0cd3b5474d8000000000000000000000000fd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000093186200000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000001241e6d24c2000000000000000000000000fd086bc7cd5c481dcc9c85ebe478a1c0b69fcbb900000000000000000000000000000000000000000000000000000000009318620000000000000000000000000000000000000000000000000014445d8d9c448700000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000062ec9380000000000000000000000000000000000000000000000000000000000000000100000000000000000000000040612df27e16f1ff160b4827847de30b02a89007000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"))
            )
            assertThat(tx.nonce, equalTo(EvmUint64("0xe8ff")))
            assertThat(tx.to, equalTo(EvmAddress("0x4775af8fef4809fe10bf05867d2b038a4b5b2146")))
            assertThat(tx.transactionIndex, equalTo(EvmUint32("0x0")))
            assertThat(tx.value, equalTo(EvmUint256("0x0")))
            assertThat(tx.type, equalTo(EvmUint8("0x78")))

        }
    }


    private fun verifyNoDownloadAttempts() {
        assertThat(batchServiceFixture.responses, empty())
    }

    private fun verifyOneBlockDownloadAttempt() {
        assertThat(batchServiceFixture.responses, hasSize(1))
        assertThat(batchServiceFixture.responses[0], isA(ArbitrumGetBlockResponse::class.java))
    }

    private fun verifyTwoBlocksDownloadAttempts() {
        assertThat(batchServiceFixture.responses, hasSize(2))
        assertThat(batchServiceFixture.responses[0], isA(ArbitrumGetBlockResponse::class.java))
        assertThat(batchServiceFixture.responses[1], isA(ArbitrumGetBlockResponse::class.java))
    }

    private fun verifyFourBlockDownloadAttempts() {
        assertThat(batchServiceFixture.responses, hasSize(4))
        assertThat(batchServiceFixture.responses[0], isA(ArbitrumGetBlockResponse::class.java))
        assertThat(batchServiceFixture.responses[1], isA(ArbitrumGetBlockResponse::class.java))
        assertThat(batchServiceFixture.responses[2], isA(ArbitrumGetBlockResponse::class.java))
        assertThat(batchServiceFixture.responses[3], isA(ArbitrumGetBlockResponse::class.java))
    }

    private val batchServiceFixture = ArbitrumBatchServiceFixture()

    private val testInstance = ArbitrumRepositoryImpl(
        path = createTempDirectory("web3proxy_block"),
        batchService = batchServiceFixture
    )

    private fun ArbitrumRepositoryImpl.list(vararg blockNumbers: String) = listBlocks(blockNumbers.map { EvmUint64(it) })
}