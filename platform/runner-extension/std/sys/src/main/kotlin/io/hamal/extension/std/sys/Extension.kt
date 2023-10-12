package io.hamal.extension.std.sys

import io.hamal.extension.std.sys.adhoc.AdhocInvokeFunction
import io.hamal.extension.std.sys.exec.ExecGetFunction
import io.hamal.extension.std.sys.exec.ExecListFunction
import io.hamal.extension.std.sys.func.*
import io.hamal.extension.std.sys.hook.HookCreateFunction
import io.hamal.extension.std.sys.namespace.NamespaceCreateFunction
import io.hamal.extension.std.sys.namespace.NamespaceGetFunction
import io.hamal.extension.std.sys.namespace.NamespaceListFunction
import io.hamal.extension.std.sys.req.ReqGetFunction
import io.hamal.extension.std.sys.topic.*
import io.hamal.extension.std.sys.trigger.TriggerCreateFunction
import io.hamal.extension.std.sys.trigger.TriggerGetFunction
import io.hamal.extension.std.sys.trigger.TriggerListFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.BundleExtension
import io.hamal.lib.kua.extension.BundleExtensionFactory
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl


class SysExtensionFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: ApiSdk = ApiSdkImpl(httpTemplate)
) : BundleExtensionFactory {
    override fun create(sandbox: Sandbox): BundleExtension {
        return BundleExtension(
            name = "sys",
            internals = mapOf(
                "await" to AwaitFunction(httpTemplate),
                "await_completed" to AwaitCompletedFunction(httpTemplate),
                "await_failed" to AwaitFailedFunction(httpTemplate),

                "adhoc" to AdhocInvokeFunction(sdk),

                "get_req" to ReqGetFunction(httpTemplate),

                "list_execs" to ExecListFunction(sdk),
                "get_exec" to ExecGetFunction(sdk),

                "create_func" to FuncCreateFunction(sdk),
                "get_func" to FuncGetFunction(sdk),
                "list_func" to FuncListFunction(sdk),
                "func_invoke" to FuncInvokeFunction(sdk),

                "create_hook" to HookCreateFunction(sdk),
                "get_hook" to HookGetFunction(sdk),
                "list_hook" to HookListFunction(sdk),

                "create_namespace" to NamespaceCreateFunction(sdk),
                "get_namespace" to NamespaceGetFunction(sdk),
                "list_namespace" to NamespaceListFunction(sdk),

                "create_topic" to TopicCreateFunction(sdk),
                "resolve_topic" to TopicResolveFunction(sdk),
                "list_topic" to TopicListFunction(sdk),
                "get_topic" to TopicGetFunction(sdk),
                "append_entry" to TopicEntryAppendFunction(sdk),
                "list_topic_entry" to TopicEntryListFunction(sdk),

                "create_trigger" to TriggerCreateFunction(sdk),
                "get_trigger" to TriggerGetFunction(sdk),
                "list_trigger" to TriggerListFunction(sdk)
            )
        )
    }
}