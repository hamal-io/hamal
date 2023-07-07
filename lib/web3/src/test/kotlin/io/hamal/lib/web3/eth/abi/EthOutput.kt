package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.util.ByteWindow
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal class AddressTest {
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

internal class Bytes32Test {
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

internal class BoolTest {
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

internal class Uint8Test {
    @Test
    fun `Uint8 output`() {
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

internal class Uint16Test {
    @Test
    fun `Uint16 output`() {
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

internal class Uint32Test {
    @Test
    fun `Uint32 output`() {
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

internal class Uint64Test {
    @Test
    fun `Uint64 output`() {
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

internal class Uint112Test {
    @Test
    fun `Uint112 output`() {
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

internal class Uint128Test {
    @Test
    fun `Uint128 output`() {
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

internal class Uint160Test {
    @Test
    fun `Uint160 output`() {
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

internal class Uint256Test {
    @Test
    fun `Uint256 output`() {
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

internal class StringTest {
    @Test
    fun `Short string out`() {
        val window = createByteWindow(
            "0x000000000000000000000000000000000000000000000000000000000000000f536f6d6553686f7274537472696e670000000000000000000000000000000000"
        )
        val testInstance = EthOutput.String("SomeString")
        val result: EthString = testInstance.decode(window)
        assertThat(result.value, equalTo("SomeShortString"))

        verifyTestInstance(testInstance)
    }

    @Test
    fun `Long string output`() {
        val window = createByteWindow(
            "0x0000000000000000000000000000000000000000000000000000000000000363225365642075742070657273706963696174697320756e6465206f6d6e69732069737465206e61747573206572726f722073697420766f6c7570746174656d206163637573616e7469756d20646f6c6f72656d717565206c617564616e7469756d2c20746f74616d2072656d206170657269616d2c2065617175652069707361207175616520616220696c6c6f20696e76656e746f726520766572697461746973206574207175617369206172636869746563746f206265617461652076697461652064696374612073756e74206578706c696361626f2e204e656d6f20656e696d20697073616d20766f6c7570746174656d207175696120766f6c7570746173207369742061737065726e6174757220617574206f646974206175742066756769742c20736564207175696120636f6e73657175756e747572206d61676e6920646f6c6f72657320656f732071756920726174696f6e6520766f6c7570746174656d207365717569206e65736369756e742e204e6571756520706f72726f20717569737175616d206573742c2071756920646f6c6f72656d20697073756d207175696120646f6c6f722073697420616d65742c20636f6e73656374657475722c2061646970697363692076656c69742c207365642071756961206e6f6e206e756d7175616d2065697573206d6f64692074656d706f726120696e636964756e74207574206c61626f726520657420646f6c6f7265206d61676e616d20616c697175616d207175616572617420766f6c7570746174656d2e20557420656e696d206164206d696e696d612076656e69616d2c2071756973206e6f737472756d20657865726369746174696f6e656d20756c6c616d20636f72706f726973207375736369706974206c61626f72696f73616d2c206e69736920757420616c697175696420657820656120636f6d6d6f646920636f6e73657175617475723f205175697320617574656d2076656c2065756d206975726520726570726568656e64657269742071756920696e20656120766f6c7570746174652076656c69742065737365207175616d206e6968696c206d6f6c65737469616520636f6e73657175617475722c2076656c20696c6c756d2071756920646f6c6f72656d2065756d206675676961742071756f20766f6c7570746173206e756c6c612070617269617475723f220000000000000000000000000000000000000000000000000000000000"
        )
        val testInstance = EthOutput.String("SomeString")
        val result = testInstance.decode(window)
        assertThat(
            result.value,
            equalTo("\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\"")
        )

        verifyTestInstance(testInstance)
    }

    private fun verifyTestInstance(testInstance: EthOutput<EthString>) {
        assertThat(testInstance.name, equalTo("SomeString"))
        assertThat(testInstance.clazz, equalTo(EthString::class))
    }
}


fun createByteWindow(hexData: String): ByteWindow {
    return if (hexData.startsWith("0x")) {
        ByteWindow.of(EthPrefixedHexString(hexData))
    } else {
        ByteWindow.of(EthHexString(hexData))
    }
}