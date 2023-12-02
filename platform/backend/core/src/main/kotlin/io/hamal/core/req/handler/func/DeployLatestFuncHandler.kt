package io.hamal.core.req.handler.func

/*
@Component
class DeployLatestFuncHandler(
    val funcRepository: FuncRepository,
    val codeQueryRepository: CodeQueryRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<FuncDeployLatestSubmitted>(FuncDeployLatestSubmitted::class) {

    override fun invoke(req: FuncDeployLatestSubmitted) {
        deployLatestVersion(req).also { emitEvent(req.cmdId(), it) }
    }
}

private fun DeployLatestFuncHandler.deployLatestVersion(req: FuncDeployLatestSubmitted): Func {
    return funcRepository.deployLatest(req.funcId, req.cmdId(), req.deployMessage)
}

private fun DeployLatestFuncHandler.emitEvent(cmdId: CmdId, func: Func) {
    eventEmitter.emit(cmdId, FuncDeployedEvent(func))
}*/
