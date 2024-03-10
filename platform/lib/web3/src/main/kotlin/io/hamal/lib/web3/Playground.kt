package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.web3.Erc20.decimals
import io.hamal.lib.web3.US.getReserves
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.*
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResponse
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

object US {
    val getReserves = EthFunction(
        "getReserves",
        EthInput.Tuple0(),
        EthOutput.Tuple3(
            EthOutput.Uint112("_reserve0"),
            EthOutput.Uint112("_reserve1"),
            EthOutput.Uint32("_blockTimestampLast")
        )
    )
}


fun main() {
    val srv = EthHttpBatchService(
        HttpTemplateImpl("http://localhost:10001")
    )

    srv.getBlock(EthUint64(10001)).execute()

    val response = srv
        .call(
            EthBatchService.EthCallRequest(
                to = EthAddress(EthPrefixedHexString("0x570febdf89c07f256c75686caca215289bb11cfc")),
                data = EthPrefixedHexString("0x0902f1ac"),
                blockNumber = EthUint64(12040753L)
            )
        ).execute()

    val x = getReserves.outputs.decodeToMap((response[0] as EthCallResponse).result)
    System.out.println(x["_reserve0"])
    System.out.println(x["_reserve1"])

    println(decimals.signature.encoded.toPrefixedHexString())

}