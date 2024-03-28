package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.Erc20.decimals
import io.hamal.lib.web3.US.getReserves
import io.hamal.lib.web3.evm.abi.*
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmHotCallResponse
import io.hamal.lib.web3.evm.http.EvmHotHttpBatchService


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
//    val ethService = EthHttpBatchService(
//        HttpTemplateImpl("http://localhost:10000/eth"),
//    )

//    val blockResponse = ethService.getBlock(EvmUint64(10001)).execute().first()
//
//

    val hotService = EvmHotHttpBatchService(
        HttpTemplateImpl("http://localhost:10000/eth"),
    )

    hotService.call(
        to = EvmAddress("0x570febdf89c07f256c75686caca215289bb11cfc"),
        data = EvmPrefixedHexString("0x0902f1ac"),
        number = EvmUint64(12040753L)
    )

    val response = hotService.execute()


//    val response = srv
//        .call(
//            EthBatchService.EthCallRequest(
//                to = EvmAddress(EvmPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
//                data = EvmPrefixedHexString("0x0902f1ac"),
//                blockNumber = EvmUint64(12040753L)
//            )
//        ).execute()
//
    val x = getReserves.outputs.decodeToMap((response[0] as EvmHotCallResponse).result?.value?.let(::EvmPrefixedHexString)!!)
    println(x["_reserve0"])
    println(x["_reserve1"])

    println(decimals.signature.encoded.toPrefixedHexString())

}