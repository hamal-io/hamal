package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.evm.abi.*
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.impl.eth.http.EthHttpBatchService


object Erc20 {

    val decimals = EthFunction(
        name = "decimals",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.Uint8("decimals"),
        )
    )

    val name = EthFunction(
        name = "name",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.String("name"),
        )
    )

    val symbol = EthFunction(
        name = "symbol",
        inputs = EthInputTuple0,
        outputs = EvmOutputTuple1(
            EvmOutput.String("symbol"),
        )
    )
}

object US {
    val getReserves = EthFunction(
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
    val ethService = EthHttpBatchService(
        HttpTemplateImpl("http://localhost:10000/eth"),
    )

    val blockResponse = ethService.getBlock(EvmUint64(10001)).execute().first()



//    val response = srv
//        .call(
//            EthBatchService.EthCallRequest(
//                to = EvmAddress(EvmPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
//                data = EvmPrefixedHexString("0x0902f1ac"),
//                blockNumber = EvmUint64(12040753L)
//            )
//        ).execute()
//
//    val x = getReserves.outputs.decodeToMap((response[0] as EthCallResponse).result)
//    System.out.println(x["_reserve0"])
//    System.out.println(x["_reserve1"])
//
//    println(decimals.signature.encoded.toPrefixedHexString())

}