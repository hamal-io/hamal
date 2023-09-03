package io.hamal.lib.sdk.hub.service

import io.hamal.lib.http.HttpTemplate

interface AccountService

internal class DefaultAccountService(
    private val template: HttpTemplate
) : AccountService