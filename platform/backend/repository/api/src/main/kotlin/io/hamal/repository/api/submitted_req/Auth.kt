package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.PasswordHash
import kotlinx.serialization.Serializable

@Serializable
data class AuthSignInWithPasswordSubmittedReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val authId: AuthId,
    val accountId: AccountId,
    val hash: PasswordHash,
    val token: AuthToken
) : SubmittedReq
