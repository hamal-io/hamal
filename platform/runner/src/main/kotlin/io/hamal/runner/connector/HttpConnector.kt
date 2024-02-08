package io.hamal.runner.connector

import io.hamal.lib.domain.vo.EventToSubmit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.http.HttpException
import io.hamal.lib.sdk.BridgeSdk
import java.net.SocketException
import java.net.SocketTimeoutException

class HttpConnector(
    private val sdk: BridgeSdk
) : Connector {

    override fun poll(): List<UnitOfWork> {
        try {
            return sdk.exec.poll().work.map {
                UnitOfWork(
                    id = it.id,
                    namespaceId = it.namespaceId,
                    groupId = it.groupId,
                    inputs = it.inputs,
                    state = it.state,
                    code = it.code,
                    correlation = it.correlation,
                    invocation = it.invocation,
                )
            }
        } catch (e: java.net.ConnectException) {
            return listOf()
        } catch (e: SocketException) {
            return listOf()
        } catch (e: SocketTimeoutException) {
            return listOf()
        } catch (e: HttpException) {
            return listOf()
        }
    }

    override fun complete(execId: ExecId, result: ExecResult, state: ExecState, events: List<EventToSubmit>) {
        sdk.exec.complete(execId, result, state, events)
    }

    override fun fail(execId: ExecId, result: ExecResult) {
        sdk.exec.fail(execId, result)
    }
}