package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.*
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
        val testInstance = EvmOutput.Address("SomeAddress")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    @Test
    fun `Prefixed hex string output`() {
        val window = createByteWindow("0x000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338")
        val testInstance = EvmOutput.Address("SomeAddress")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmAddress) {
        assertThat(result.value, equalTo(EvmUint160(BigInteger("1086584542116516189292563230522663967077286712120"))))
        assertThat(result.toString(), equalTo("0xBe5422D15F39373Eb0a97Ff8c10Fbd0e40e29338"))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmAddress>) {
        assertThat(testInstance.name, equalTo("SomeAddress"))
        assertThat(testInstance.clazz, equalTo(EvmAddress::class))
    }
}

internal object Bytes32Test {
    @Test
    fun `Byte32 output`() {
        val window: ByteWindow = createByteWindow("0x00000000000000000000000000000000000000000000000000000000ffffffff")

        val testInstance = EvmOutput.Byte32("SomeBytes32")
        val result = testInstance.decode(window)
        assertResult(result)

        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmBytes32) {
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

    private fun verifyTestInstance(testInstance: EvmOutput<EvmBytes32>) {
        assertThat(testInstance.name, equalTo("SomeBytes32"))
        assertThat(testInstance.clazz, equalTo(EvmBytes32::class))
    }
}

internal object BoolTest {
    @Test
    fun `Bool output of true`() {
        val window = createByteWindow("0x0000000000000000000000000000000000000000000000000000000000000001")
        val testInstance = EvmOutput.Bool("SomeBool")
        val result = testInstance.decode(window)
        assertTrue(result.value)
        verifyTestInstance(testInstance)
    }

    @Test
    fun `Bool output of false`() {
        val window = createByteWindow("0x0000000000000000000000000000000000000000000000000000000000000000")
        val testInstance = EvmOutput.Bool("SomeBool")
        val result = testInstance.decode(window)
        assertFalse(result.value)
        verifyTestInstance(testInstance)
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmBool>) {
        assertThat(testInstance.name, equalTo("SomeBool"))
        assertThat(testInstance.clazz, equalTo(EvmBool::class))
    }
}

internal object Uint8Test {
    @Test
    fun `Max uint8 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000000000000000000000000000000000ff")
        val testInstance = EvmOutput.Uint8("SomeUint8")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint8) {
        assertThat(result.value, equalTo(BigInteger("255")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint8>) {
        assertThat(testInstance.name, equalTo("SomeUint8"))
        assertThat(testInstance.clazz, equalTo(EvmUint8::class))
    }
}

internal object Uint16Test {
    @Test
    fun `Max uint16 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000000000000000000000000000ffff")
        val testInstance = EvmOutput.Uint16("SomeUint16")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint16) {
        assertThat(result.value, equalTo(BigInteger("65535")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint16>) {
        assertThat(testInstance.name, equalTo("SomeUint16"))
        assertThat(testInstance.clazz, equalTo(EvmUint16::class))
    }
}

internal object Uint32Test {
    @Test
    fun `Max uint32 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000000000000000000000000000ffffffff")
        val testInstance = EvmOutput.Uint32("SomeUint32")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint32) {
        assertThat(result.value, equalTo(BigInteger("4294967295")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint32>) {
        assertThat(testInstance.name, equalTo("SomeUint32"))
        assertThat(testInstance.clazz, equalTo(EvmUint32::class))
    }
}

internal object Uint64Test {
    @Test
    fun `Max uint64 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000000000000000ffffffffffffffff")
        val testInstance = EvmOutput.Uint64("SomeUint64")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint64) {
        assertThat(result.value, equalTo(BigInteger("18446744073709551615")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint64>) {
        assertThat(testInstance.name, equalTo("SomeUint64"))
        assertThat(testInstance.clazz, equalTo(EvmUint64::class))
    }
}

internal object Uint112Test {
    @Test
    fun `Max uint112 output`() {
        val window = createByteWindow("0x000000000000000000000000000000000000ffffffffffffffffffffffffffff")
        val testInstance = EvmOutput.Uint112("SomeUint112")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint112) {
        assertThat(result.value, equalTo(BigInteger("5192296858534827628530496329220095")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint112>) {
        assertThat(testInstance.name, equalTo("SomeUint112"))
        assertThat(testInstance.clazz, equalTo(EvmUint112::class))
    }
}

internal object Uint128Test {
    @Test
    fun `Max uint128 output`() {
        val window = createByteWindow("0x00000000000000000000000000000000ffffffffffffffffffffffffffffffff")
        val testInstance = EvmOutput.Uint128("SomeUint128")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint128) {
        assertThat(result.value, equalTo(BigInteger("340282366920938463463374607431768211455")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint128>) {
        assertThat(testInstance.name, equalTo("SomeUint128"))
        assertThat(testInstance.clazz, equalTo(EvmUint128::class))
    }
}

internal object Uint160Test {
    @Test
    fun `Max uint160 output`() {
        val window = createByteWindow("0x000000000000000000000000ffffffffffffffffffffffffffffffffffffffff")
        val testInstance = EvmOutput.Uint160("SomeUint160")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint160) {
        assertThat(result.value, equalTo(BigInteger("1461501637330902918203684832716283019655932542975")))
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint160>) {
        assertThat(testInstance.name, equalTo("SomeUint160"))
        assertThat(testInstance.clazz, equalTo(EvmUint160::class))
    }
}

internal object Uint256Test {
    @Test
    fun `Max uint256 output`() {
        val window = createByteWindow("0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
        val testInstance = EvmOutput.Uint256("SomeUint256")
        val result = testInstance.decode(window)
        assertResult(result)
        verifyTestInstance(testInstance)
    }

    private fun assertResult(result: EvmUint256) {
        assertThat(
            result.value,
            equalTo(BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935"))
        )
    }

    private fun verifyTestInstance(testInstance: EvmOutput<EvmUint256>) {
        assertThat(testInstance.name, equalTo("SomeUint256"))
        assertThat(testInstance.clazz, equalTo(EvmUint256::class))
    }
}

internal object StringTest {
    @Test
    fun `Short string out`() {
        val window = createByteWindow(
            "0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000006554e492d56320000000000000000000000000000000000000000000000000000"
        )
        val testInstance = EvmOutput.String("SomeString")
        val result: EvmString = testInstance.decode(window)
        assertThat(result.value, equalTo("UNI-V2"))

        assertThat(testInstance.name, equalTo("SomeString"))
        assertThat(testInstance.clazz, equalTo(EvmString::class))
    }
}

internal object OutputTuple0Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(EvmPrefixedHexString("0x123456"))
        assertThat(result, equalTo(mapOf()))
    }

    @Test
    fun `decode() - Always empty`() {
        val result = testInstance.decode(EvmPrefixedHexString("0x123456"))
        assertThat(result, equalTo(listOf()))
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo(""))
    }

    private val testInstance = EvmOutputTuple0
}

internal object OutputTuple1Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(EvmPrefixedHexString("0x123456"))
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EvmUint112(BigInteger.valueOf(1193046))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(EvmPrefixedHexString("0x123456"))
        assertThat(
            result.toList(), equalTo(
                listOf(
                    DecodedEvmType("Arg1", EvmUint112(BigInteger.valueOf(1193046)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint112"))
    }

    private val testInstance = EvmOutputTuple1(EvmOutput.Uint112("Arg1"))
}


internal object OutputTuple2Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(
            EvmPrefixedHexString("0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000040")
        )
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EvmUint32(BigInteger.valueOf(32)),
                    "Arg2" to EvmUint64(BigInteger.valueOf(64))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(
            EvmPrefixedHexString("0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000040")
        )
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEvmType("Arg1", EvmUint32(BigInteger.valueOf(32))),
                    DecodedEvmType("Arg2", EvmUint64(BigInteger.valueOf(64)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint32,uint64"))
    }

    private val testInstance = EvmOutputTuple2(
        EvmOutput.Uint32("Arg1"),
        EvmOutput.Uint64("Arg2")
    )
}

internal object OutputTuple3Test {
    @Test
    fun `decodeToMap() - Always empty`() {
        val result = testInstance.decodeToMap(
            EvmPrefixedHexString("0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080")
        )
        assertThat(
            result, equalTo(
                mapOf(
                    "Arg1" to EvmUint32(BigInteger.valueOf(32)),
                    "Arg2" to EvmUint64(BigInteger.valueOf(64)),
                    "Arg3" to EvmUint128(BigInteger.valueOf(128))
                )
            )
        )
    }

    @Test
    fun `decode()`() {
        val result = testInstance.decode(
            EvmPrefixedHexString("0x000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000400000000000000000000000000000000000000000000000000000000000000080")
        )
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEvmType("Arg1", EvmUint32(BigInteger.valueOf(32))),
                    DecodedEvmType("Arg2", EvmUint64(BigInteger.valueOf(64))),
                    DecodedEvmType("Arg3", EvmUint128(BigInteger.valueOf(128)))
                )
            )
        )
    }

    @Test
    fun `concatenatedTypes()`() {
        val result = testInstance.concatenatedTypes()
        assertThat(result, equalTo("uint32,uint64,uint128"))
    }

    private val testInstance = EvmOutputTuple3(
        EvmOutput.Uint32("Arg1"),
        EvmOutput.Uint64("Arg2"),
        EvmOutput.Uint128("Arg3")
    )
}

fun createByteWindow(hexData: String): ByteWindow {
    return if (hexData.startsWith("0x")) {
        ByteWindow.of(EvmPrefixedHexString(hexData))
    } else {
        ByteWindow.of(EvmHexString(hexData))
    }
}