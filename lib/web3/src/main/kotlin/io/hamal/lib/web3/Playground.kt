package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.eth.abi.EthFunction
import io.hamal.lib.web3.eth.abi.EthInputTuple0
import io.hamal.lib.web3.eth.abi.EthOutput
import io.hamal.lib.web3.eth.abi.EthOutputTuple3
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService


fun main() {
    val srv = EthHttpBatchService(
        HttpTemplate("https://cloudflare-eth.com")
    )

    val getReserves = EthFunction(
        name = "getReserves",
        inputs = EthInputTuple0,
        outputs = EthOutputTuple3(
            EthOutput.Uint112("_reserve0"),
            EthOutput.Uint112("_reserve1"),
            EthOutput.Uint32("_blockTimestampLast")
        )
    )

    val response = srv
//        .call(
//        EthBatchService.EthCallRequest(
//            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
//            data = EthPrefixedHexString("0x0902f1ac"),
//            blockNumber = EthUint64(12040752L)
//        )
//    )
        .callFunction(
            to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
            function = getReserves,
            blockNumber = EthUint64(12040752L)
        )
        .execute().first()


    require(response is EthCallResp)
    println(response)

    val x = getReserves.outputs.decodeToMap(response.result)
    System.out.println(x["_reserve0"])
    System.out.println(x["_reserve1"])
}