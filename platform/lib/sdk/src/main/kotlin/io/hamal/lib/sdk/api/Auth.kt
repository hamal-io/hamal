package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.LogInReq
import io.hamal.request.LogOutReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiLoginReq(
    override val username: AccountName,
    override val password: Password
) : LogInReq

@Serializable
data class ApiLogoutReq(
    override val id: AccountId,
    override val token: AuthToken
) : LogOutReq

@Serializable
data class ApiTokenSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val defaultFlowIds: Map<GroupId, FlowId>,
    val token: AuthToken,
    val name: AccountName
) : ApiSubmitted

@Serializable
data class ApiConvertAccountSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val accountId: AccountId,
    val token: AuthToken,
    val name: AccountName
) : ApiSubmitted

@Serializable
data class ApiLogoutSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val accountId: AccountId
) : ApiSubmitted

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplate
) : AuthService