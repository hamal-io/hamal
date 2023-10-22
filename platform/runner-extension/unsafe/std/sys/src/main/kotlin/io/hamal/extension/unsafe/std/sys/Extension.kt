package io.hamal.extension.unsafe.std.sys

import io.hamal.extension.unsafe.std.sys.func.*
import io.hamal.extension.unsafe.std.sys.snippet.SnippetCreateFunction
import io.hamal.extension.unsafe.std.sys.snippet.SnippetGetFunction
import io.hamal.extension.unsafe.std.sys.snippet.SnippetUpdateFunction
import io.hamal.extension.unsafe.std.sys.topic.*
import io.hamal.extension.unsafe.std.sys.adhoc.AdhocInvokeFunction
import io.hamal.extension.unsafe.std.sys.code.CodeGetFunction
import io.hamal.extension.unsafe.std.sys.exec.ExecGetFunction
import io.hamal.extension.unsafe.std.sys.exec.ExecListFunction
import io.hamal.extension.unsafe.std.sys.hook.HookCreateFunction
import io.hamal.extension.unsafe.std.sys.namespace.NamespaceCreateFunction
import io.hamal.extension.unsafe.std.sys.namespace.NamespaceGetFunction
import io.hamal.extension.unsafe.std.sys.namespace.NamespaceListFunction
import io.hamal.extension.unsafe.std.sys.req.ReqGetFunction
import io.hamal.extension.unsafe.std.sys.trigger.TriggerCreateFunction
import io.hamal.extension.unsafe.std.sys.trigger.TriggerGetFunction
import io.hamal.extension.unsafe.std.sys.trigger.TriggerListFunction
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtensionFactory
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.ApiSdkImpl


class SysExtensionFactory(
    private val httpTemplate: HttpTemplate,
    private val sdk: ApiSdk = ApiSdkImpl(httpTemplate)
) : RunnerUnsafeExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerUnsafeExtension {
        return RunnerUnsafeExtension(
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