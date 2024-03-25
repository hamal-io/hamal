package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint112
import io.hamal.lib.web3.evm.abi.type.EvmUint32
import io.hamal.lib.web3.evm.abi.DecodedEvmType
import io.hamal.lib.web3.evm.abi.EthFunction
import io.hamal.lib.web3.evm.abi.EvmInput
import io.hamal.lib.web3.evm.abi.EvmOutput
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.math.BigInteger

internal object FuncTest {
    @Test
    fun `decode() - getReserves`() {
        val someData = EvmPrefixedHexString(
            "0x0000000000000000000000000000000000000000000000c18146ba0b7fb26e350000000000000000000000000000000000000000000e720af711d2090dc245c2000000000000000000000000000000000000000000000000000000006212aa48"
        )
        val testInstance = EthFunction(
            "getReserves",
            EvmInput.Tuple0(),
            EvmOutput.Tuple3(
                EvmOutput.Uint112("_reserve0"),
                EvmOutput.Uint112("_reserve1"),
                EvmOutput.Uint32("_blockTimestampLast")
            )
        )

        val result = testInstance.decode(someData)
        assertThat(
            result, equalTo(
                listOf(
                    DecodedEvmType("_reserve0", EvmUint112(BigInteger("3569536943663755718197"))),
                    DecodedEvmType("_reserve1", EvmUint112(BigInteger("17463513524334423395419586"))),
                    DecodedEvmType("_blockTimestampLast", EvmUint32(BigInteger("1645390408")))
                )
            )
        )
    }

    @Test
    fun `decodeToMap() - getReserves`() {
        val someData = EvmPrefixedHexString(
            "0x0000000000000000000000000000000000000000000000c18146ba0b7fb26e350000000000000000000000000000000000000000000e720af711d2090dc245c2000000000000000000000000000000000000000000000000000000006212aa48"
        )
        val testInstance = EthFunction(
            "getReserves",
            EvmInput.Tuple0(),
            EvmOutput.Tuple3(
                EvmOutput.Uint112("_reserve0"),
                EvmOutput.Uint112("_reserve1"),
                EvmOutput.Uint32("_blockTimestampLast")
            )
        )

        val result = testInstance.decodeToMap(someData)
        assertThat(
            result, equalTo(
                mapOf(
                    "_reserve0" to EvmUint112(BigInteger("3569536943663755718197")),
                    "_reserve1" to EvmUint112(BigInteger("17463513524334423395419586")),
                    "_blockTimestampLast" to EvmUint32(BigInteger("1645390408"))
                )
            )
        )
    }
}