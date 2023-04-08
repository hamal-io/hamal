package io.hamal.worker.extension.starter

import io.hamal.worker.extension.api.WorkerExtensionFunction

class StartFactory : WorkerExtensionFunction.Factory {
    override fun invoke(): WorkerExtensionFunction = StartFunction()
}