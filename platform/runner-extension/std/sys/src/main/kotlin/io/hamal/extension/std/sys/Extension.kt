package io.hamal.extension.std.sys

import io.hamal.extension.std.sys.adhoc.InvokeAdhocFunction
import io.hamal.extension.std.sys.exec.GetExecFunction
import io.hamal.extension.std.sys.exec.ListExecFunction
import io.hamal.extension.std.sys.func.CreateFuncFunction
import io.hamal.extension.std.sys.func.GetFuncFunction
import io.hamal.extension.std.sys.func.ListFuncFunction
import io.hamal.extension.std.sys.namespace.CreateNamespaceFunction
import io.hamal.extension.std.sys.namespace.GetNamespaceFunction
import io.hamal.extension.std.sys.namespace.ListNamespaceFunction
import io.hamal.extension.std.sys.req.GetReqFunction
import io.hamal.extension.std.sys.topic.*
import io.hamal.extension.std.sys.trigger.CreateTriggerFunction
import io.hamal.extension.std.sys.trigger.GetTriggerFunction
import io.hamal.extension.std.sys.trigger.ListTriggerFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.ApiSdk

class SysExtensionFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: ApiSdk = ApiSdkImpl(httpTemplate)
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "sys",
            internals = mapOf(
                "await" to AwaitFunction(httpTemplate),
                "await_completed" to AwaitCompletedFunction(httpTemplate),
                "await_failed" to AwaitFailedFunction(httpTemplate),

                "adhoc" to InvokeAdhocFunction(sdk),

                "get_req" to GetReqFunction(httpTemplate),

                "list_execs" to ListExecFunction(sdk),
                "get_exec" to GetExecFunction(sdk),

                "create_func" to CreateFuncFunction(sdk),
                "get_func" to GetFuncFunction(sdk),
                "list_func" to ListFuncFunction(sdk),

                "create_namespace" to CreateNamespaceFunction(sdk),
                "get_namespace" to GetNamespaceFunction(sdk),
                "list_namespace" to ListNamespaceFunction(sdk),

                "create_topic" to CreateTopicFunction(sdk),
                "resolve_topic" to ResolveTopicFunction(sdk),
                "list_topic" to ListTopicFunction(sdk),
                "get_topic" to GetTopicFunction(sdk),
                "append_entry" to AppendToTopicFunction(sdk),
                "list_topic_entry" to ListTopicEntryFunction(sdk),

                "create_trigger" to CreateTriggerFunction(sdk),
                "get_trigger" to GetTriggerFunction(sdk),
                "list_trigger" to ListTriggerFunction(sdk)
            )
        )
    }
}