package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.evm.abi.*
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.http.EvmHotHttpBatchService
import io.hamal.lib.web3.evm.rlp.RlpValue
import io.hamal.lib.web3.util.ByteUtils
import io.hamal.lib.web3.util.Web3Formatter
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import java.math.BigInteger
import java.nio.ByteBuffer


object Erc20 {

    val decimals = EvmFunction(
        name = "decimals",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.Uint8("decimals"),
        )
    )

    val name = EvmFunction(
        name = "name",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.String("name"),
        )
    )

    val symbol = EvmFunction(
        name = "symbol",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.String("symbol"),
        )
    )
}

object US {
    val getReserves = EvmFunction(
        "getReserves",
        EvmInput.Tuple0(),
        EvmOutput.Tuple3(
            EvmOutput.Uint112("_reserve0"),
            EvmOutput.Uint112("_reserve1"),
            EvmOutput.Uint32("_blockTimestampLast")
        )
    )
}

fun main() {

    val key = "..."

    val credentials = Credentials.create(key)

    val values = RlpValue.List(
        RlpValue.String(11155111), // chainId
        RlpValue.String(6), // nonce
        RlpValue.String(25000000000000), // maxPriorityFeePerGas
        RlpValue.String(25000000000000), // maxFeePerGas
        RlpValue.String(21000), // gas limit
        RlpValue.String(EvmAddress("0x6Cc9397c3B38739daCbfaA68EaD5F5D77Ba5F455").value.value), // to
        RlpValue.String(BigInteger("231123112311231123")), // value
        RlpValue.String(""), // data
        RlpValue.List.empty, // accessList
    )

    val encoded = values.toByteArray()

    val encodedTransaction = ByteBuffer.allocate(encoded.size + 1)
        .put(0x02.toByte())
        .put(encoded)
        .array()

    println(values)

    println(Web3Formatter.formatToHex(encodedTransaction))

    println(credentials.address)

    val signatureData = Sign.signMessage(encodedTransaction, credentials.ecKeyPair)

    val valuesWithSignature = RlpValue.List(
        RlpValue.String(11155111), // chainId
        RlpValue.String(6), // nonce
        RlpValue.String(25000000000000), // maxPriorityFeePerGas
        RlpValue.String(25000000000000), // maxFeePerGas
        RlpValue.String(21000), // gas limit
        RlpValue.String(EvmAddress("0x6Cc9397c3B38739daCbfaA68EaD5F5D77Ba5F455").value.value), // to
        RlpValue.String(BigInteger("231123112311231123")), // value
        RlpValue.String(""), // data
        RlpValue.List.empty, // accessList
        RlpValue.String(Sign.getRecId(signatureData, 11155111)), // recId
        RlpValue.String(ByteUtils.trimLeadingZeroes(signatureData.r)), // r
        RlpValue.String(ByteUtils.trimLeadingZeroes(signatureData.s)), // s
    )

    val resultEncoded = valuesWithSignature.toByteArray()

    val result = ByteBuffer.allocate(resultEncoded.size + 1)
        .put(0x02.toByte())
        .put(resultEncoded)
        .array()

    println(Web3Formatter.formatToHex(result))

    // 0xd0e30db0

//    HttpTemplateImpl(".....")
//        .post()
//        .body(
//            """
//            {
//                "jsonrpc": "2.0",
//                "method": "eth_sendRawTransaction",
//                "params": [
//                    "0x${Web3Formatter.formatToHex(result)}"
//                ],
//                "id": 1
//            }
//        """.trimIndent()
//        )
//        .execute {
//            require(this is HttpSuccessResponse)
//            println(String(inputStream.readAllBytes()))
//        }

    EvmHotHttpBatchService(
        HttpTemplateImpl("....")
    ).also { service ->

    }


//    val ethService = EthHttpBatchService(
//        HttpTemplateImpl("http://localhost:10000/eth"),
//    )
//
//    ethService.call(
//        to = EvmAddress("0x570febdf89c07f256c75686caca215289bb11cfc"),
//        data = EvmPrefixedHexString("0x0902f1ac"),
//        number = EvmUint64(12040753L)
//    )
//
//    val response = ethService.execute()
//
//
//    val x = getReserves.outputs.decodeToMap((response[0] as EthCallResponse).result?.value?.let(::EvmPrefixedHexString)!!)
//    println(x["_reserve0"])
//    println(x["_reserve1"])
//
//    println(decimals.signature.encoded.toPrefixedHexString())
}