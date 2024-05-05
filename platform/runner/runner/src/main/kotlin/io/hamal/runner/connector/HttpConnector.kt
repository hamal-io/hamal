package io.hamal.runner.connector

import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.domain.vo.ExecStatusCode
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
                    execToken = it.execToken,
                    namespaceId = it.namespaceId,
                    workspaceId = it.workspaceId,
                    inputs = it.inputs,
                    state = it.state,
                    code = it.code,
                    codeType = it.codeType,
                    correlation = it.correlation
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

    override fun complete(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult,
        state: ExecState,
        events: List<EventToSubmit>
    ) {
        sdk.exec.complete(execId, statusCode, result, state, events)
    }

    override fun fail(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult
    ) {
        sdk.exec.fail(execId, statusCode, result)
    }
}