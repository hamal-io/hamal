package io.hamal.lib.sdk.hub

import io.hamal.lib.http.HttpTemplate


interface HubAuthService

internal class DefaultHubAuthService(
    private val template: HttpTemplate
) : HubAuthService