package io.hamal.plugin.std.sys

import io.hamal.plugin.std.sys.func.*
import io.hamal.plugin.std.sys.snippet.SnippetCreateFunction
import io.hamal.plugin.std.sys.snippet.SnippetGetFunction
import io.hamal.plugin.std.sys.snippet.SnippetUpdateFunction
import io.hamal.plugin.std.sys.topic.*
import io.hamal.plugin.std.sys.adhoc.AdhocInvokeFunction
import io.hamal.plugin.std.sys.code.CodeGetFunction
import io.hamal.plugin.std.sys.exec.ExecGetFunction
import io.hamal.plugin.std.sys.exec.ExecListFunction
import io.hamal.plugin.std.sys.hook.HookCreateFunction
import io.hamal.plugin.std.sys.namespace.NamespaceCreateFunction
import io.hamal.plugin.std.sys.namespace.NamespaceGetFunction
import io.hamal.plugin.std.sys.namespace.NamespaceListFunction
import io.hamal.plugin.std.sys.req.ReqGetFunction
import io.hamal.plugin.std.sys.trigger.TriggerCreateFunction
import io.hamal.plugin.std.sys.trigger.TriggerGetFunction
import io.hamal.plugin.std.sys.trigger.TriggerListFunction
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl


class SysPluginFactory(
    private val httpTemplate: HttpTemplateImpl,
    private val sdk: ApiSdk = ApiSdkImpl(httpTemplate)
) : RunnerPluginExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        return RunnerPluginExtension(
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

                "snippet_create" to SnippetCreateFunction(sdk),
                "snippet_get" to SnippetGetFunction(sdk),
                "snippet_update" to SnippetUpdateFunction(sdk),

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