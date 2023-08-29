package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.util.ByteWindow
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal object AddressTest {
    @Test
    fun `Hex string output`() {
        val window = createByteWindow("000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338")
        val testInstance = EthOutput.Address("SomeAddress")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    @Test
    fun `Prefixed hex string output`() {
        val window = createByteWindow("0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338")
        val testInstance = EthOutput.Address("SomeAddress")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthAddress) {
        assertThat(result.value, equalTo(EthUint160(BigInteger("1086584542116516189292563230522663967077286712120"))))
        assertThat(result.toString(), equalTo("0xBe5422D15F39373Eb0a97Ff8c10Fbd0e40e29338"))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthAddress>) {
        assertThat(testInstance.name, equalTo("SomeAddress"))
        assertThat(testInstance.clazz, equalTo(EthAddress::class))
    }
}

internal object Bytes32Test {
    @Test
    fun `Byte32 output`() {
        val window: ByteWindow = createByteWindow("0x00000000000000000000000000000000000000000000000000000000ffffffff")

        val testInstance = EthOutput.Byte32("SomeBytes32")
        val result = testInstance.decode(window)
        assertResult(result)

        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthBytes32) {
        assertThat(
            result.value, equalTo(
                byteArrayOf(
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, -1, -1, -1, -1
                )
            )
        )
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthBytes32>) {
        assertThat(testInstance.name, equalTo("SomeBytes32"))
        assertThat(testInstance.clazz, equalTo(EthBytes32::class))
    }
}

internal object BoolTest {
    @Test
    fun `Bool output of true`() {
        val window = createByteWindow("0x0000000000000000000000000000000000000000000000000000000000000001")
        val testInstance = EthOutput.Bool("SomeBool")
        val result = testInstance.decode(window)
        assertTrue(result.value)
        verifyTestInstance(testInstance)
    }

    @Test
    fun `Bool output of false`() {
        val window = createByteWindow("0x0000000000000000000000000000000000000000000000000000000000000000")
        val testInstance = EthOutput.Bool("SomeBool")
        val result = testInstance.decode(window)
        assertFalse(result.value)
        verifyTestInstance(testInstance)
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthBool>) {
        assertThat(testInstance.name, equalTo("SomeBool"))
        assertThat(testInstance.clazz, equalTo(EthBool::class))
    }
}

internal object Uint8Test {
    @Test
    fun `Max uint8 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000000000000000000000000000000000ff")
        val testInstance = EthOutput.Uint8("SomeUint8")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint8) {
        assertThat(result.value, equalTo(BigInteger("255")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint8>) {
        assertThat(testInstance.name, equalTo("SomeUint8"))
        assertThat(testInstance.clazz, equalTo(EthUint8::class))
    }
}

internal object Uint16Test {
    @Test
    fun `Max uint16 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000000000000000000000000000ffff")
        val testInstance = EthOutput.Uint16("SomeUint16")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint16) {
        assertThat(result.value, equalTo(BigInteger("65535")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint16>) {
        assertThat(testInstance.name, equalTo("SomeUint16"))
        assertThat(testInstance.clazz, equalTo(EthUint16::class))
    }
}

internal object Uint32Test {
    @Test
    fun `Max uint32 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000000000000000000000000000ffffffff")
        val testInstance = EthOutput.Uint32("SomeUint32")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint32) {
        assertThat(result.value, equalTo(BigInteger("4294967295")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint32>) {
        assertThat(testInstance.name, equalTo("SomeUint32"))
        assertThat(testInstance.clazz, equalTo(EthUint32::class))
    }
}

internal object Uint64Test {
    @Test
    fun `Max uint64 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000000000000000ffffffffffffffff")
        val testInstance = EthOutput.Uint64("SomeUint64")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint64) {
        assertThat(result.value, equalTo(BigInteger("18446744073709551615")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint64>) {
        assertThat(testInstance.name, equalTo("SomeUint64"))
        assertThat(testInstance.clazz, equalTo(EthUint64::class))
    }
}

internal object Uint112Test {
    @Test
    fun `Max uint112 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000ffffffffffffffffffffffffffff")
        val testInstance = EthOutput.Uint112("SomeUint112")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint112) {
        assertThat(result.value, equalTo(BigInteger("5192296858534827628530496329220095")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint112>) {
        assertThat(testInstance.name, equalTo("SomeUint112"))
        assertThat(testInstance.clazz, equalTo(EthUint112::class))
    }
}

internal object Uint128Test {
    @Test
    fun `Max uint128 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000ffffffffffffffffffffffffffffffff")
        val testInstance = EthOutput.Uint128("SomeUint128")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint128) {
        assertThat(result.value, equalTo(BigInteger("340282366920938463463374607431768211455")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint128>) {
        assertThat(testInstance.name, equalTo("SomeUint128"))
        assertThat(testInstance.clazz, equalTo(EthUint128::class))
    }
}

internal object Uint160Test {
    @Test
    fun `Max uint160 output`() {
        val window = createByteWindow("0x000000000000000000000000ffffffffffffffffffffffffffffffffffffffff")
        val testInstance = EthOutput.Uint160("SomeUint160")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint160) {
        assertThat(result.value, equalTo(BigInteger("1461501637330902918203684832716283019655932542975")))
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint160>) {
        assertThat(testInstance.name, equalTo("SomeUint160"))
        assertThat(testInstance.clazz, equalTo(EthUint160::class))
    }
}

internal object Uint256Test {
    @Test
    fun `Max uint256 output`() {
        val window = createByteWindow("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
        val testInstance = EthOutput.Uint256("SomeUint256")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EthUint256) {
        assertThat(
            result.value,
            equalTo(BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935"))
        )
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthUint256>) {
        assertThat(testInstance.name, equalTo("SomeUint256"))
        assertThat(testInstance.clazz, equalTo(EthUint256::class))
    }
}

internal object StringTest {
    @Test
    fun `Short string out`() {
        val window = createByteWindow(
            "0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000006554e492d56320000000000000000000000000000000000000000000000000000"
        )
        val testInstance = EthOutput.String("SomeString")
        val result: EthString = testInstance.decode(window)
        assertThat(result.value, equalTo("UNI-V2"))

        assertThat(testInstance.name, equalTo("SomeString"))
        assertThat(testInstance.clazz, equalTo(EthString::class))
    }
}

internal object OutputTuple0Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(EthPrefixedHexString("0x123456"))
        assertThat(result, equalTo(mapOf()))
    }

    @Test
    fun `decode() - Always empty`() {
        val result = testInstance.decode(EthPrefixedHexString("0x123456"))
        assertThat(result, equalTo(listOf()))
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo(""))
    }

    private val testInstance = EthOutputTuple0
}

internal object OutputTuple1Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(EthPrefixedHexString("0x123456"))
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EthUint112(BigInteger.valueOf(1193046))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(EthPrefixedHexString("0x123456"))
        assertThat(
            result.toList(), equalTo(
                listOf(
                    DecodedEthType("Arg1", EthUint112(BigInteger.valueOf(1193046)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint112"))
    }

    private val testInstance = EthOutputTuple1(EthOutput.Uint112("Arg1"))
}


internal object OutputTuple2Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(
            EthPrefixedHexString("0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000040")
        )
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EthUint32(BigInteger.valueOf(32)),
                    "Arg2" to EthUint64(BigInteger.valueOf(64))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(
            EthPrefixedHexString("0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000040")
        )
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEthType("Arg1", EthUint32(BigInteger.valueOf(32))),
                    DecodedEthType("Arg2", EthUint64(BigInteger.valueOf(64)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint32,uint64"))
    }

    private val testInstance = EthOutputTuple2(
        EthOutput.Uint32("Arg1"),
        EthOutput.Uint64("Arg2")
    )
}

internal object OutputTuple3Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(
            EthPrefixedHexString("0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080")
        )
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EthUint32(BigInteger.valueOf(32)),
                    "Arg2" to EthUint64(BigInteger.valueOf(64)),
                    "Arg3" to EthUint128(BigInteger.valueOf(128))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(
            EthPrefixedHexString("0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080")
        )
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEthType("Arg1", EthUint32(BigInteger.valueOf(32))),
                    DecodedEthType("Arg2", EthUint64(BigInteger.valueOf(64))),
                    DecodedEthType("Arg3", EthUint128(BigInteger.valueOf(128)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint32,uint64,uint128"))
    }

    private val testInstance = EthOutputTuple3(
        EthOutput.Uint32("Arg1"),
        EthOutput.Uint64("Arg2"),
        EthOutput.Uint128("Arg3")
    )
}

fun createByteWindow(hexData: String): ByteWindow {
    return if (hexData.startsWith("0x")) {
        ByteWindow.of(EthPrefixedHexString(hexData))
    } else {
        ByteWindow.of(EthHexString(hexData))
    }
}