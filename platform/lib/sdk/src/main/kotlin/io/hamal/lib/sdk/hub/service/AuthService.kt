package io.hamal.lib.sdk.hub.service

import io.hamal.lib.http.HttpTemplate

interface AuthService

internal class DefaultAuthService(
    private val httpTemplate: HttpTemplate
) : AuthService