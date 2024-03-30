package io.hamal.lib.web3.evm.rlp

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal object RlpEncoderTest {

    //@formatter:off
    @TestFactory
    fun encode() = listOf(
        RlpValue.String("dog") to byteArrayOf(0x83, 'd', 'o', 'g'),

        RlpValue.List(
            RlpValue.String("cat"),
            RlpValue.String("dog"),
        ) to byteArrayOf(0xc8, 0x83, 'c', 'a', 't', 0x83, 'd', 'o', 'g'),

        RlpValue.String(0) to byteArrayOf(0x80),
        RlpValue.String("") to byteArrayOf(0x80),

        RlpValue.String(15) to byteArrayOf(0x0f),
        RlpValue.String(0x0f) to byteArrayOf(0x0f),
        RlpValue.String(1024) to byteArrayOf(0x82, 0x04, 0x00),

        RlpValue.List(
            RlpValue.List(),
            RlpValue.List(RlpValue.List()),
            RlpValue.List(RlpValue.List(), RlpValue.List(RlpValue.List()))
        ) to byteArrayOf(0xc7, 0xc0, 0xc1, 0xc0, 0xc3, 0xc0, 0xc1, 0xc0) ,

        RlpValue.String("Lorem ipsum dolor sit amet, consectetur adipisicing elit")
        to byteArrayOf(
            0xb8, 0x38, 'L','o','r','e','m',' ','i','p','s','u','m',' ','d','o','l','o','r',' ','s','i','t',' ','a','m','e','t',',',' ','c','o','n','s','e','c',
            't','e','t','u','r',' ','a','d','i','p','i','s','i','c','i','n','g',' ','e','l','i','t'
        )

    ).map { (given, expected) ->
        dynamicTest(given.toString()) {
            val result = EncodeRlp(given)
            assertThat(result, equalTo(expected))
        }
    }

    //@formatter:on

    private fun byteArrayOf(vararg bytes: Any): ByteArray {
        return bytes.map {
            when (it) {
                is Int -> it.toByte()
                is Char -> it.code.toByte()
                else -> TODO()
            }
        }.toByteArray()
    }
}

