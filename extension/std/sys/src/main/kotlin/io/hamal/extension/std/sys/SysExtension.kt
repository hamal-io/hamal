package io.hamal.extension.std.sys

import io.hamal.extension.std.sys.exec.GetExecFunction
import io.hamal.extension.std.sys.exec.InvokeAdhocFunction
import io.hamal.extension.std.sys.exec.ListExecFunction
import io.hamal.extension.std.sys.func.*
import io.hamal.extension.std.sys.namespace.CreateNamespaceFunction
import io.hamal.extension.std.sys.req.GetReqFunction
import io.hamal.extension.std.sys.topic.CreateTopicFunction
import io.hamal.extension.std.sys.topic.GetTopicFunction
import io.hamal.extension.std.sys.topic.ListTopicFunction
import io.hamal.extension.std.sys.trigger.CreateTriggerFunction
import io.hamal.extension.std.sys.trigger.GetTriggerFunction
import io.hamal.extension.std.sys.trigger.ListTriggerFunction
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.HttpTemplateSupplier

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "sys",
            internals = mapOf(
                "await" to AwaitFunction(templateSupplier),
                "await_completed" to AwaitCompletedFunction(templateSupplier),
                "await_failed" to AwaitFailedFunction(templateSupplier),

                "adhoc" to InvokeAdhocFunction(templateSupplier),

                "get_req" to GetReqFunction(templateSupplier),

                "list_execs" to ListExecFunction(templateSupplier),
                "get_exec" to GetExecFunction(templateSupplier),

                "create_func" to CreateFuncFunction(templateSupplier),
                "get_func" to GetFuncFunction(templateSupplier),
                "list_func" to ListFuncFunction(templateSupplier),

                "create_namespace" to CreateNamespaceFunction(templateSupplier),
                "get_namespace" to GetNamespaceFunction(templateSupplier),
                "list_namespace" to ListNamespaceFunction(templateSupplier),

                "create_topic" to CreateTopicFunction(templateSupplier),
                "list_topic" to ListTopicFunction(templateSupplier),
                "get_topic" to GetTopicFunction(templateSupplier),

                "create_trigger" to CreateTriggerFunction(templateSupplier),
                "get_trigger" to GetTriggerFunction(templateSupplier),
                "list_trigger" to ListTriggerFunction(templateSupplier)

            )
        )
    }
}