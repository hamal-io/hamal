package io.hamal.lib.sdk.hub.service

import io.hamal.lib.http.HttpTemplate

interface GroupService

internal class DefaultGroupService(
    private val httpTemplate: HttpTemplate
) : GroupService