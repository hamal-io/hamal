package io.hamal.app.web3proxy.eth.repository

import io.hamal.app.web3proxy.fixture.EthBatchServiceFixture
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.io.path.createTempDirectory

internal class ListByBlockNumbersTest {

    @Test
    fun `Does nothing on empty block number list`() {
        testInstance.list(listOf()).also { result ->
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
        ethBatchServiceFixture.clear()
    }

    private fun assertBlock0x1284810(block: EthBlock?) {
        checkNotNull(block)
        assertThat(block.extraData, equalTo(EthBytes32("0x546974616e2028746974616e6275696c6465722e78797a29")))
        assertThat(block.hash, equalTo(EthHash("0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")))
        assertThat(
            block.logsBloom,
            equalTo(EthPrefixedHexString("0x40b30d48e16500351a2210288d8a9a2310d82f744f40041100995012d02a434245553590c42a6c61ebb0384248132177e3259218ba4269f0f6f52d9052a8ec29e87247394a228c3fbd02f76f942304f59d2641c1517589284e813d42a0e61b48981d042216061e5c101cdac482625c65f2776c682e105721c64dc09218eb10057c36b2da2a1443c8004f325a622209c554724d97fb4b30eb7436304ae25226238b0456923796a2905f04f4d4b9da049815518640e4acba0fae7d348c1a0094f4ed57302244b50004a82888c240ad3ace7d7200af942b0052e4573bba1054a844b172a0041c49962a510425d7a440a8c3dcf9b4080a7e064008bd281070937c0d"))
        )
        assertThat(block.miner, equalTo(EthAddress("0x4838b106fce9647bdf1e7877bf73ce8b0bad5f97")))
        assertThat(block.mixHash, equalTo(EthHash("0x312b2e833821b694a1789d9efc7618a3a600cc06c7e4ed4757860c998e69081b")))
        assertThat(block.number, equalTo(EthUint64("0x1284810")))
        assertThat(block.parentHash, equalTo(EthHash("0x3da1516115fd35fbe3c4d83f7422bf7d5f973b08f09028ea18ae27346181da52")))
        assertThat(block.receiptsRoot, equalTo(EthHash("0x1f47a6666ca0f3038ec6e0fc236f58b2aad8933efc24e7f9c28d66c39cc2a45e")))
        assertThat(block.sha3Uncles, equalTo(EthHash("0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")))
        assertThat(block.size, equalTo(EthUint64("0xfbec")))
        assertThat(block.stateRoot, equalTo(EthHash("0xfaca9e1209e82e72e5700512d0cfedcb156baca691b1cc56e80ea72e4d26dbaf")))
        assertThat(block.timestamp, equalTo(EthUint64("0x65eff103")))
        assertThat(block.gasLimit, equalTo(EthUint64("0x1c9c380")))
        assertThat(block.gasUsed, equalTo(EthUint64("0xa0adf6")))
        assertThat(block.totalDifficulty, equalTo(EthUint256("0xc70d815d562d3cfa955")))
        assertThat(block.transactions, hasSize(3))

        block.transactions[1].also { tx ->
            assertThat(tx.blockHash, equalTo(EthHash("0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")))
            assertThat(tx.blockNumber, equalTo(EthUint64("0x1284810")))
            assertThat(tx.from, equalTo(EthAddress("0x7088659e96e5dcc91608d871937bdb5fa0776bac")))
            assertThat(tx.gas, equalTo(EthUint64("0xdbba0")))
            assertThat(tx.gasPrice, equalTo(EthUint64("0xcdeac4a95")))
            assertThat(tx.maxPriorityFeePerGas, equalTo(EthUint64("0x1")))
            assertThat(tx.maxFeePerGas, equalTo(EthUint64("0xf719b8cb1")))
            assertThat(tx.hash, equalTo(EthHash("0x93508d92b49b73aea5b8394a0394dac83abd81f9dba4424b2604e43108245b33")))
            assertThat(
                tx.input,
                equalTo(
                    EthPrefixedHexString(
                        "0xe16dd1ef0000000000000000000000000000000000000000000000000000000000000080000000000000000000000000b3f7b16349364589d1615f2ca9f29f67a1878e70000000000000000000000000000000000000000000013e2d09ff7efe0f392603000000000000000000000000000000000000000000000000005d77f46b83db200000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000c7e66cb8ff0e00000000000000000000000000000000000000000000000dc86b8172f313049bf270000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000028c02aaa39b223fe8d0a0e5c4f27ead9083c756cc21c9922314ed1415c95b9fd453c3818fd41867d0b000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000058d8305238780000000000000000000000000000000000000000000000061a651e84fccdeef66dc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000028c02aaa39b223fe8d0a0e5c4f27ead9083c756cc21c9922314ed1415c95b9fd453c3818fd41867d0b000000000000000000000000000000000000000000000000"
                    )
                )
            )
            assertThat(tx.nonce, equalTo(EthUint64("0x3288")))
            assertThat(tx.to, equalTo(EthAddress("0xc6fecdf760af24095cded954de7d81ab49f8bae1")))
            assertThat(tx.transactionIndex, equalTo(EthUint32("0x1")))
            assertThat(tx.value, equalTo(EthUint256("0x0")))
            assertThat(tx.type, equalTo(EthUint8("0x2")))
            assertThat(tx.accessList, empty())
        }

        assertThat(block.withdrawals, hasSize(2))
        assertThat(block.withdrawalsRoot, equalTo(EthHash("0x79373dfb0ff17072206ea0dae4942afa3791a9699aeacdb74db8a4f8b62b374f")))

    }

    private fun assertBlock0x1284811(block: EthBlock?) {
        checkNotNull(block)

        assertThat(block.number, equalTo(EthUint64("0x1284811")))
        assertThat(block.transactions, hasSize(1))

        block.transactions[0].also { tx ->
            assertThat(tx.blockHash, equalTo(EthHash("0x900dfa22a267c8d5df9020bd479ba83d7fd5acbe04f3e8df1c17e99dd0f24b7b")))
            assertThat(tx.blockNumber, equalTo(EthUint64("0x1284811")))
            assertThat(tx.from, equalTo(EthAddress("0x315d2ee4fccda0def532ef4108ff57204f8d9eba")))
            assertThat(tx.gas, equalTo(EthUint64("0xaae60")))
            assertThat(tx.gasPrice, equalTo(EthUint64("0x1e09a5fcef")))
            assertThat(tx.maxPriorityFeePerGas, equalTo(EthUint64("0x1e09a5fcef")))
            assertThat(tx.maxFeePerGas, equalTo(EthUint64("0x1e09a5fcef")))
            assertThat(tx.hash, equalTo(EthHash("0x98196c1e86e3f6a40b6163a5eef99ae42e9531284be495703f75d668d3622fba")))
            assertThat(
                tx.input,
                equalTo(EthPrefixedHexString("0xf8ec05b502030a8e189f91c87c8f0b35cbf2c6ba5e4701bd2004000000000000000000000000e7259580000000000000ebf94fcd4d7b200000007e20e121ded9ed7c67b4971eed536e8f82873df300dac17f958d2ee523a2206206994597c13d831ec70001f41c9922314ed1415c95b9fd453c3818fd41867d0b02db44a4a457c87225b5ba45f27b7828a4cc03c112c02aaa39b223fe8d0a0e5c4f27ead9083c756cc2000000000000000002d6353cbaf74720000000000000001c8b4fc2beb603819562d63efdfdc7804666725f4cc8cb45c1a41a080900"))
            )
            assertThat(tx.nonce, equalTo(EthUint64("0x30035")))
            assertThat(tx.to, equalTo(EthAddress("0x738e79fbc9010521763944ddf13aad7f61502221")))
            assertThat(tx.transactionIndex, equalTo(EthUint32("0x0")))
            assertThat(tx.value, equalTo(EthUint256("0x0")))
            assertThat(tx.type, equalTo(EthUint8("0x2")))
            assertThat(tx.accessList, hasSize(6))


            tx.accessList!![0].also { item ->
                assertThat(item.address, equalTo(EthAddress("0x6123b0049f904d730db3c36a31167d9d4121fa6b")))
                assertThat(item.storageKeys, hasSize(3))
                assertThat(item.storageKeys[0], equalTo(EthHash("0x0000000000000000000000000000000000000000000000000000000000000006")))
                assertThat(item.storageKeys[1], equalTo(EthHash("0x2bb5dee39b6d8067750b2768c936a1015076542d5146ae8ded66775d13e793d8")))
                assertThat(item.storageKeys[2], equalTo(EthHash("0x042e48394e269bebc2e84ae150cf622009358c6eb88ce48df2852d64234c7b65")))
            }
        }

        assertThat(block.withdrawals, hasSize(1))
        block.withdrawals!![0].also { withdrawal ->
            assertThat(withdrawal.index, equalTo(EthUint64("0x2459406")))
            assertThat(withdrawal.validatorIndex, equalTo(EthUint64("0x11016e")))
            assertThat(withdrawal.address, equalTo(EthAddress("0xb9d7934878b5fb9610b3fe8a5e441e8fad7e293f")))
            assertThat(withdrawal.amount, equalTo(EthUint64("0x1176aed")))
        }
        assertThat(block.withdrawalsRoot, equalTo(EthHash("0x282392326ea162791facce2d95ceda9409bae15736a81434d9cc23b0aa491258")))
    }

    private fun verifyNoDownloadAttempts() {
        assertThat(ethBatchServiceFixture.responses, empty())
    }

    private fun verifyOneBlockDownloadAttempt() {
        assertThat(ethBatchServiceFixture.responses, hasSize(1))
        assertThat(ethBatchServiceFixture.responses[0], isA(EthGetBlockResponse::class.java))
    }

    private fun verifyTwoBlocksDownloadAttempts() {
        assertThat(ethBatchServiceFixture.responses, hasSize(2))
        assertThat(ethBatchServiceFixture.responses[0], isA(EthGetBlockResponse::class.java))
        assertThat(ethBatchServiceFixture.responses[1], isA(EthGetBlockResponse::class.java))
    }

    private fun verifyFourBlockDownloadAttempts() {
        assertThat(ethBatchServiceFixture.responses, hasSize(4))
        assertThat(ethBatchServiceFixture.responses[0], isA(EthGetBlockResponse::class.java))
        assertThat(ethBatchServiceFixture.responses[1], isA(EthGetBlockResponse::class.java))
        assertThat(ethBatchServiceFixture.responses[2], isA(EthGetBlockResponse::class.java))
        assertThat(ethBatchServiceFixture.responses[3], isA(EthGetBlockResponse::class.java))
    }

    private val ethBatchServiceFixture = EthBatchServiceFixture()

    private val testInstance = EthBlockRepositoryImpl(
        path = createTempDirectory("web3proxy_block"),
        addressRepository = EthAddressRepositoryImpl(createTempDirectory("web3proxy_address")),
        ethBatchService = ethBatchServiceFixture
    )

    private fun EthBlockRepositoryImpl.list(vararg blockNumbers: String) = list(blockNumbers.map { EthUint64(it) })
}