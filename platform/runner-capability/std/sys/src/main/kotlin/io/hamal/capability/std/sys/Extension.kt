package io.hamal.capability.std.sys

import io.hamal.capability.std.sys.adhoc.AdhocInvokeFunction
import io.hamal.capability.std.sys.code.CodeGetFunction
import io.hamal.capability.std.sys.exec.ExecGetFunction
import io.hamal.capability.std.sys.exec.ExecListFunction
import io.hamal.capability.std.sys.func.*
import io.hamal.capability.std.sys.hook.HookCreateFunction
import io.hamal.capability.std.sys.namespace.NamespaceCreateFunction
import io.hamal.capability.std.sys.namespace.NamespaceGetFunction
import io.hamal.capability.std.sys.namespace.NamespaceListFunction
import io.hamal.capability.std.sys.req.ReqGetFunction
import io.hamal.capability.std.sys.topic.*
import io.hamal.capability.std.sys.trigger.TriggerCreateFunction
import io.hamal.capability.std.sys.trigger.TriggerGetFunction
import io.hamal.capability.std.sys.trigger.TriggerListFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.capability.Capability
import io.hamal.lib.kua.capability.CapabilityFactory
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl


class SysCapabilityFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: ApiSdk = ApiSdkImpl(httpTemplate)
) : CapabilityFactory {
    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "sys",
            internals = mapOf(
                "await" to AwaitFunction(httpTemplate),
                "await_completed" to AwaitCompletedFunction(httpTemplate),
                "await_failed" to AwaitFailedFunction(httpTemplate),

                "adhoc" to AdhocInvokeFunction(sdk),
                "code_get" to CodeGetFunction(sdk),

                "req_get" to ReqGetFunction(httpTemplate),

                "exec_list" to ExecListFunction(sdk),
                "exec_get" to ExecGetFunction(sdk),

                "func_create" to FuncCreateFunction(sdk),
                "func_get" to FuncGetFunction(sdk),
                "func_list" to FuncListFunction(sdk),
                "func_invoke" to FuncInvokeFunction(sdk),

                "hook_create" to HookCreateFunction(sdk),
                "hook_get" to HookGetFunction(sdk),
                "hook_list" to HookListFunction(sdk),

                "namespace_create" to NamespaceCreateFunction(sdk),
                "namespace_get" to NamespaceGetFunction(sdk),
                "namespace_list" to NamespaceListFunction(sdk),

                "topic_create" to TopicCreateFunction(sdk),
                "topic_resolve" to TopicResolveFunction(sdk),
                "topic_list" to TopicListFunction(sdk),
                "topic_get" to TopicGetFunction(sdk),
                "topic_entry_append" to TopicEntryAppendFunction(sdk),
                "topic_entry_list" to TopicEntryListFunction(sdk),

                "trigger_create" to TriggerCreateFunction(sdk),
                "trigger_get" to TriggerGetFunction(sdk),
                "trigger_list" to TriggerListFunction(sdk)
            )
        )
    }
}