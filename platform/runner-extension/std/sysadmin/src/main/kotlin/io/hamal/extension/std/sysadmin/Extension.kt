package io.hamal.extension.std.sysadmin


import io.hamal.extension.std.sysadmin.adhoc.InvokeAdhocFunction
import io.hamal.extension.std.sysadmin.exec.GetExecFunction
import io.hamal.extension.std.sysadmin.exec.ListExecFunction
import io.hamal.extension.std.sysadmin.func.CreateFuncFunction
import io.hamal.extension.std.sysadmin.func.GetFuncFunction
import io.hamal.extension.std.sysadmin.func.ListFuncFunction
import io.hamal.extension.std.sysadmin.namespace.CreateNamespaceFunction
import io.hamal.extension.std.sysadmin.namespace.GetNamespaceFunction
import io.hamal.extension.std.sysadmin.namespace.ListNamespaceFunction
import io.hamal.extension.std.sysadmin.req.GetReqFunction
import io.hamal.extension.std.sysadmin.topic.*
import io.hamal.extension.std.sysadmin.trigger.CreateTriggerFunction
import io.hamal.extension.std.sysadmin.trigger.GetTriggerFunction
import io.hamal.extension.std.sysadmin.trigger.ListTriggerFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.AdminSdk
import io.hamal.lib.sdk.AdminSdkImpl

class SysAdminExtensionFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: AdminSdk = AdminSdkImpl(httpTemplate)
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "sysadmin",
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