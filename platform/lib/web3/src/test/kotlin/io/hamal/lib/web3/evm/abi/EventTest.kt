//package io.hamal.lib.web3.eth.abi
//
//import io.hamal.lib.web3.eth.abi.type.EthAddress
//import io.hamal.lib.web3.eth.abi.type.EthHash
//import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
//import io.hamal.lib.web3.eth.abi.type.EthUint256
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.hamcrest.Matchers.hasSize
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import java.math.BigInteger
//
//internal object EthEventTest {
//
//    @Test
//    fun decodeToMap() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple1(EthOutput.Uint256("value"))
//        )
//        val result = testInstance.decodeToMap(topics, data)
//        assertThat(
//            result, equalTo(
//                mapOf(
//                    "from" to EthAddress(EthPrefixedHexString("0x00B26Eb6BA3a5Ce29a1F4B0dDB5Ecf30E8EF54Ea")),
//                    "to" to EthAddress(EthPrefixedHexString("0x933e56C32334e93D23a9d6477e5BF59fB47559B6")),
//                    "value" to EthUint256(BigInteger("2839980000000000000000"))
//                )
//            )
//        )
//    }
//
//    @Test
//    fun decode() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple1(EthOutput.Uint256("value"))
//        )
//        val result = testInstance.decode(topics, data)
//        assertThat(
//            result, equalTo(
//                listOf(
//                    DecodedEthType(
//                        "from",
//                        EthAddress(EthPrefixedHexString("0x00B26Eb6BA3a5Ce29a1F4B0dDB5Ecf30E8EF54Ea"))
//                    ),
//                    DecodedEthType(
//                        "to",
//                        EthAddress(EthPrefixedHexString("0x933e56C32334e93D23a9d6477e5BF59fB47559B6"))
//                    ),
//                    DecodedEthType("value", EthUint256(BigInteger("2839980000000000000000")))
//                )
//            )
//        )
//    }
//
//    @Test
//    fun `decode() - No topics provided - anonymous events are not supported`() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple1(EthOutput.Uint256("value"))
//        )
//
//        val exception = assertThrows<IllegalArgumentException> {
//            testInstance.decode(listOf(), data)
//        }
//        assertThat(exception.message, equalTo("Topics must not be empty"))
//    }
//
//    @Test
//    fun `decode() - Unable to decode event with different topic information`() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple1(EthOutput.Uint256("value"))
//        )
//
//        val exception = assertThrows<IllegalArgumentException> {
//            testInstance.decode(notMatchingSignatureTopics, data)
//        }
//        assertThat(exception.message, equalTo("Unable to decode event from different topic"))
//    }
//
//    @Test
//    fun `decode() - Event does not has any data`() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple0()
//        )
//
//        val result = testInstance.decode(withoutDataTopics, data)
//        assertThat(result, hasSize(2))
//        assertThat(
//            result[0],
//            equalTo(
//                DecodedEthType(
//                    "from",
//                    EthAddress(EthPrefixedHexString("0x00B26Eb6BA3a5Ce29a1F4B0dDB5Ecf30E8EF54Ea"))
//                )
//            )
//        )
//        assertThat(
//            result[1],
//            equalTo(
//                DecodedEthType(
//                    "to",
//                    EthAddress(EthPrefixedHexString("0x933e56C32334e93D23a9d6477e5BF59fB47559B6"))
//                )
//            )
//        )
//    }
//
//
//    @Test
//    fun `signature() - Creates signature event Transfer(address,address,uint256)`() {
//        val testInstance = EthEvent(
//            "Transfer",
//            EthOutput.Tuple2(
//                EthOutput.Address("from"),
//                EthOutput.Address("to")
//            ),
//            EthOutput.Tuple1(EthOutput.Uint256("value"))
//        )
//        val result = testInstance.signature
//        assertThat(result.value, equalTo("Transfer(address,address,uint256)"))
//        assertThat(
//            result.encoded,
//            equalTo(EthHash(EthPrefixedHexString("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")))
//        )
//    }
//
//
//    private val topics = listOf(
//        EthHash(EthPrefixedHexString("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")),
//        EthHash(EthPrefixedHexString("0x00000000000000000000000000b26eb6ba3a5ce29a1f4b0ddb5ecf30e8ef54ea")),
//        EthHash(EthPrefixedHexString("0x000000000000000000000000933e56c32334e93d23a9d6477e5bf59fb47559b6"))
//    )
//
//    private val notMatchingSignatureTopics = listOf(
//        EthHash(EthPrefixedHexString("0x0000000000000000000000000000000000000000000000000000000000000000"))
//    )
//
//    private val singleTopic = listOf(
//        EthHash(EthPrefixedHexString("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"))
//    )
//
//    private val withoutDataTopics = listOf(
//        EthHash(EthPrefixedHexString("0x5339b6d009a5c0c018fede74de3deceec24de196bf12a0bbd0664cb31064d3f3")),
//        EthHash(EthPrefixedHexString("0x00000000000000000000000000b26eb6ba3a5ce29a1f4b0ddb5ecf30e8ef54ea")),
//        EthHash(EthPrefixedHexString("0x000000000000000000000000933e56c32334e93d23a9d6477e5bf59fb47559b6"))
//    )
//
//    private val data = EthPrefixedHexString("0x000000000000000000000000000000000000000000000099f4a3cb241dde0000")
//}