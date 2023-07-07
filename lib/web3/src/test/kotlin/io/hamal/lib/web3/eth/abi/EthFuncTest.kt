package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint112
import io.hamal.lib.web3.eth.abi.type.EthUint32
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal object EthFuncTest {
    @Test
    fun `decode() - getReserves`() {
        val someData = EthPrefixedHexString(
            "0x0000000000000000000000000000000000000000000000c18146ba0b7fb26e350000000000000000000000000000000000000000000e720af711d2090dc245c2000000000000000000000000000000000000000000000000000000006212aa48"
        )
        val testInstance = EthFunc(
            "getReserves",
            EthInput.Tuple0(),
            EthOutput.Tuple3(
                EthOutput.Uint112("_reserve0"),
                EthOutput.Uint112("_reserve1"),
                EthOutput.Uint32("_blockTimestampLast")
            )
        )

        val result = testInstance.decode(someData)
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEthType("_reserve0", EthUint112(BigInteger("3569536943663755718197"))),
                    DecodedEthType("_reserve1", EthUint112(BigInteger("17463513524334423395419586"))),
                    DecodedEthType("_blockTimestampLast", EthUint32(BigInteger("1645390408")))
                )
            )
        )
    }

    @Test
    fun `decodeToMap() - getReserves`() {
        val someData = EthPrefixedHexString(
            "0x0000000000000000000000000000000000000000000000c18146ba0b7fb26e350000000000000000000000000000000000000000000e720af711d2090dc245c2000000000000000000000000000000000000000000000000000000006212aa48"
        )
        val testInstance = EthFunc(
            "getReserves",
            EthInput.Tuple0(),
            EthOutput.Tuple3(
                EthOutput.Uint112("_reserve0"),
                EthOutput.Uint112("_reserve1"),
                EthOutput.Uint32("_blockTimestampLast")
            )
        )

        val result = testInstance.decodeToMap(someData)
        assertThat(
            result, equalTo(
                mapOf(
                    "_reserve0" to EthUint112(BigInteger("3569536943663755718197")),
                    "_reserve1" to EthUint112(BigInteger("17463513524334423395419586")),
                    "_blockTimestampLast" to EthUint32(BigInteger("1645390408"))
                )
            )
        )
    }
}