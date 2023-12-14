package io.hamal.lib.sdk.api

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.request.AuthChallengeMetaMaskReq
import io.hamal.request.AuthLogInEmailReq
import io.hamal.request.AuthLogInMetaMaskReq
import kotlinx.serialization.Serializable

@Serializable
data class ApiAuthChallengeMetaMaskReq(
    override val address: Web3Address,
) : AuthChallengeMetaMaskReq


@Serializable
data class ApiChallengeMetaMask(
    val challenge: Web3Challenge
)

@Serializable
data class ApiAuthLoginMetaMaskReq(
    override val address: Web3Address,
    override val signature: Web3Signature
) : AuthLogInMetaMaskReq

@Serializable
data class ApiAuthLoginEmailReq(
    override val email: Email,
    override val password: Password
) : AuthLogInEmailReq

@Serializable
data class ApiTokenSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val accountId: AccountId,
    val groupIds: List<GroupId>,
    val defaultFlowIds: Map<GroupId, FlowId>,
    val token: AuthToken
) : ApiSubmitted

@Serializable
data class ApiAccountConvertSubmitted(
    override val id: ReqId,
    override val status: ReqStatus,
    val accountId: AccountId,
    val token: AuthToken
) : ApiSubmitted

interface AuthService

internal class ApiAuthServiceImpl(
    private val template: HttpTemplate
) : AuthService