package io.hamal.extension.std.sys

import io.hamal.extension.std.sys.adhoc.InvokeAdhocFunction
import io.hamal.extension.std.sys.exec.GetExecFunction
import io.hamal.extension.std.sys.exec.ListExecFunction
import io.hamal.extension.std.sys.func.*
import io.hamal.extension.std.sys.namespace.CreateNamespaceFunction
import io.hamal.extension.std.sys.req.GetReqFunction
import io.hamal.extension.std.sys.topic.*
import io.hamal.extension.std.sys.trigger.CreateTriggerFunction
import io.hamal.extension.std.sys.trigger.GetTriggerFunction
import io.hamal.extension.std.sys.trigger.ListTriggerFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.lib.sdk.HubSdk

class SysExtensionFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: HubSdk = DefaultHubSdk(httpTemplate)
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

                "create_topic" to CreateTopicFunction(httpTemplate),
                "resolve_topic" to ResolveTopicFunction(DefaultHubSdk(httpTemplate)),
                "list_topic" to ListTopicFunction(httpTemplate),
                "get_topic" to GetTopicFunction(httpTemplate),
                "append_entry" to AppendToTopicFunction(httpTemplate),
                "list_topic_entry" to ListTopicEntryFunction(httpTemplate),

                "create_trigger" to CreateTriggerFunction(httpTemplate),
                "get_trigger" to GetTriggerFunction(httpTemplate),
                "list_trigger" to ListTriggerFunction(httpTemplate)
            )
        )
    }
}