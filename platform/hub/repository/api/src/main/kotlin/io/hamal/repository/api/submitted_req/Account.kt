package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
data class SubmittedCreateAccountWithPasswordReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    val id: AccountId,
    val groupId: GroupId,
    val name: AccountName,
    val email: AccountEmail?,
    val password: AccountPassword
) : SubmittedReq