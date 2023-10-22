package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.bridge.BridgeExecService
import io.hamal.lib.sdk.bridge.BridgeExecServiceImpl

interface BridgeSdk {
    val exec: BridgeExecService
}

data class BridgeSdkImpl(
    val template: HttpTemplateImpl
) : BridgeSdk {

    override val exec: BridgeExecService by lazy {
        BridgeExecServiceImpl(template)
    }

}