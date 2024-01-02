package io.hamal.lib.domain.submitted

import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.*

data class ExtensionCreateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val extensionId: ExtensionId,
    val name: ExtensionName,
    val codeId: CodeId,
    val code: CodeValue
) : Submitted()

data class ExtensionUpdateSubmitted(
    override val id: ReqId,
    override var status: ReqStatus,
    val groupId: GroupId,
    val extensionId: ExtensionId,
    val name: ExtensionName?,
    val code: CodeValue?
) : Submitted()

