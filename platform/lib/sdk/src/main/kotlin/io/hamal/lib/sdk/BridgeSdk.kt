package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.bridge.BridgeExecService
import io.hamal.lib.sdk.bridge.DefaultBridgeExecService

interface BridgeSdk {
    val exec: BridgeExecService
}

data class DefaultBridgeSdk(
    val template: HttpTemplate
) : BridgeSdk {

    override val exec: BridgeExecService by lazy {
        DefaultBridgeExecService(template)
    }

}