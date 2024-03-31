package io.hamal.lib.web3.evm.rlp

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal object RlpDecoderTest {

    //@formatter:off
    @TestFactory
    fun encode() = listOf(
        byteArrayOf(0x83, 'd', 'o', 'g') to RlpValue.String("dog") ,

        byteArrayOf(0xc8, 0x83, 'c', 'a', 't', 0x83, 'd', 'o', 'g') to      RlpValue.List(
            RlpValue.String("cat"),
            RlpValue.String("dog"),
        ) ,

        byteArrayOf(0x80) to RlpValue.String(0) ,
        byteArrayOf(0x80) to RlpValue.String("") ,

        byteArrayOf(0x0f) to RlpValue.String(15) ,
        byteArrayOf(0x0f) to RlpValue.String(0x0f) ,
        byteArrayOf(0x82, 0x04, 0x00) to RlpValue.String(1024) ,

        byteArrayOf(0xc7, 0xc0, 0xc1, 0xc0, 0xc3, 0xc0, 0xc1, 0xc0) to RlpValue.List(
            RlpValue.List(),
            RlpValue.List(RlpValue.List()),
            RlpValue.List(RlpValue.List(), RlpValue.List(RlpValue.List()))
        )  ,

        byteArrayOf(
            0xb8, 0x38, 'L','o','r','e','m',' ','i','p','s','u','m',' ','d','o','l','o','r',' ','s','i','t',' ','a','m','e','t',',',' ','c','o','n','s','e','c',
            't','e','t','u','r',' ','a','d','i','p','i','s','i','c','i','n','g',' ','e','l','i','t'
        ) to RlpValue.String("Lorem ipsum dolor sit amet, consectetur adipisicing elit")

    ).map { (given, expected) ->
        dynamicTest(expected.toString()) {
            val result = DecodeRlp(given)
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