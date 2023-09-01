package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.Erc20.decimals
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.EthFunction
import io.hamal.lib.web3.eth.abi.EthInputTuple0
import io.hamal.lib.web3.eth.abi.EthOutput
import io.hamal.lib.web3.eth.abi.EthOutputTuple1
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.http.EthHttpBatchService


object Erc20 {

    val decimals = EthFunction(
        name = "decimals",
        inputs = EthInputTuple0,
        outputs = EthOutputTuple1(
            EthOutput.Uint8("decimals"),
        )
    )

    val name = EthFunction(
        name = "name",
        inputs = EthInputTuple0,
        outputs = EthOutputTuple1(
            EthOutput.String("name"),
        )
    )

    val symbol = EthFunction(
        name = "symbol",
        inputs = EthInputTuple0,
        outputs = EthOutputTuple1(
            EthOutput.String("symbol"),
        )
    )
}


fun main() {
    val srv = EthHttpBatchService(
        HttpTemplate("http://localhost:8000")
    )


    val response = srv
        .call(
        EthBatchService.EthCallRequest(
            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
            data = EthPrefixedHexString("0x0902f1ac"),
            blockNumber = EthUint64(12040752L)
        )
    )
//        .callFunction(
//            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
//            function = getReserves,
//            blockNumber = EthUint64(12040752L)
//        )
//        .execute().first()
//
//
//    require(response is EthCallResp)
//    println(response)
//
//    val x = getReserves.outputs.decodeToMap(response.result)
//    System.out.println(x["_reserve0"])
//    System.out.println(x["_reserve1"])

//    val response = srv
////        .call(
////        EthBatchService.EthCallRequest(
////            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
////            data = EthPrefixedHexString("0x0902f1ac"),
////            blockNumber = EthUint64(12040752L)
////        )
////    )
////        .callFunction(
////            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
////            function = getReserves,
////            blockNumber = EthUint64(12040752L)
////        )
////        .callFunction(
////            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
////            function = decimals,
////            blockNumber = EthUint64(12040752L)
////        )
//        .call(
//            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
//            function = decimals,
//            blockNumber = EthUint64(12040752L)
//        )
//        .execute().first()
//
//    println(response)
//
//
//    require(response is EthCallResponse)
//
//    val x = decimals.outputs.decodeToMap(response.result)
//
//    println(x)

    println(decimals.signature.encoded.toPrefixedHexString())

}