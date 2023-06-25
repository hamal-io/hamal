package io.hamal.backend.instance.web.exec

import io.hamal.backend.instance.web.BaseRouteIT
import io.hamal.backend.repository.api.ExecCmdRepository.*
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Exec
import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpStatusCode
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.http.body
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal sealed class BaseExecRouteIT : BaseRouteIT() {
    fun createAdhocExec(): SubmittedInvokeAdhocReq {
        val createAdhocExecResponse = httpTemplate
            .post("/v1/adhoc")
            .body(
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    secrets = InvocationSecrets(),
                    code = Code("40 + 2")
                )
            )
            .execute()

        assertThat(createAdhocExecResponse.statusCode, equalTo(HttpStatusCode.Accepted))
        require(createAdhocExecResponse is SuccessHttpResponse) { "request was not successful" }

        return createAdhocExecResponse.result(SubmittedInvokeAdhocReq::class)
    }

    fun createExec(status: ExecStatus): Exec {

        val planedExec = execCmdRepository.plan(
            PlanCmd(
                id = CmdId(1),
                execId = generateDomainId(::ExecId),
                correlation = null,
                inputs = ExecInputs(),
                secrets = ExecSecrets(),
                code = Code("")
            )
        )

        if (status == ExecStatus.Planned) {
            return planedExec
        }

        val scheduled = execCmdRepository.schedule(
            ScheduleCmd(
                id = CmdId(2),
                execId = planedExec.id,
            )
        )

        if (status == ExecStatus.Scheduled) {
            return scheduled
        }

        val queued = execCmdRepository.queue(
            QueueCmd(
                id = CmdId(3),
                execId = scheduled.id
            )
        )
        if (status == ExecStatus.Queued) {
            return queued
        }

        val startedExec = execCmdRepository.start(StartCmd(CmdId(4))).first()
        if (status == ExecStatus.Started) {
            return startedExec
        }

        return when (status) {
            ExecStatus.Completed -> execCmdRepository.complete(CompleteCmd(id = CmdId(5), execId = startedExec.id))
            ExecStatus.Failed -> execCmdRepository.fail(FailCmd(id = CmdId(5), execId = startedExec.id))
            else -> TODO()
        }
    }
}