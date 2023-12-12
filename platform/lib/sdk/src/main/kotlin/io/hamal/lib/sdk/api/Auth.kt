package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.ChallengeMetaMaskReq
import io.hamal.request.LogInMetaMaskReq
import io.hamal.request.LogInPasswordReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiChallengeMetaMaskReq(
    override val address: Web3Address,
) : ChallengeMetaMaskReq


@Serializable
data class ApiChallengeMetaMask(
    val challenge: Web3Challenge
)

@Serializable
data class ApiLoginMetaMaskReq(
    override val address: Web3Address,
    override val signature: Web3Signature
) : LogInMetaMaskReq

@Serializable
data class ApiLoginPasswordReq(
    override val name: AccountName,
    override val password: Password
) : LogInPasswordReq

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

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplate
) : AuthService