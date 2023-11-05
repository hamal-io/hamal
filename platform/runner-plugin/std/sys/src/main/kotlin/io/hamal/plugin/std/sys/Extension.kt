package io.hamal.plugin.std.sys

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.std.sys.adhoc.AdhocFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintCreateFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintGetFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintUpdateFunction
import io.hamal.plugin.std.sys.code.CodeGetFunction
import io.hamal.plugin.std.sys.exec.ExecGetFunction
import io.hamal.plugin.std.sys.exec.ExecListFunction
import io.hamal.plugin.std.sys.extension.ExtensionCreateFunction
import io.hamal.plugin.std.sys.extension.ExtensionGetFunction
import io.hamal.plugin.std.sys.extension.ExtensionListFunction
import io.hamal.plugin.std.sys.extension.ExtensionUpdateFunction
import io.hamal.plugin.std.sys.func.*
import io.hamal.plugin.std.sys.hook.HookCreateFunction
import io.hamal.plugin.std.sys.namespace.NamespaceCreateFunction
import io.hamal.plugin.std.sys.namespace.NamespaceGetFunction
import io.hamal.plugin.std.sys.namespace.NamespaceListFunction
import io.hamal.plugin.std.sys.req.ReqGetFunction
import io.hamal.plugin.std.sys.topic.*
import io.hamal.plugin.std.sys.trigger.TriggerCreateFunction
import io.hamal.plugin.std.sys.trigger.TriggerGetFunction
import io.hamal.plugin.std.sys.trigger.TriggerListFunction


class SysPluginFactory(
        private val sdk: ApiSdkImpl
) : RunnerPluginExtensionFactory {
    override fun create(sandbox: Sandbox): RunnerPluginExtension {
        return RunnerPluginExtension(
                name = "sys",
                internals = mapOf(
                        "await" to AwaitFunction(sdk.template),
                        "await_completed" to AwaitCompletedFunction(sdk.template),
                        "await_failed" to AwaitFailedFunction(sdk.template),

                        "adhoc" to AdhocFunction(sdk),

                        "blueprint_create" to BlueprintCreateFunction(sdk),
                        "blueprint_get" to BlueprintGetFunction(sdk),
                        "blueprint_update" to BlueprintUpdateFunction(sdk),

                        "code_get" to CodeGetFunction(sdk),

                        "req_get" to ReqGetFunction(sdk.template),

                        "exec_list" to ExecListFunction(sdk),
                        "exec_get" to ExecGetFunction(sdk),

                        "extension_create" to ExtensionCreateFunction(sdk),
                        "extension_get" to ExtensionGetFunction(sdk),
                        "extension_list" to ExtensionListFunction(sdk),
                        "extension_update" to ExtensionUpdateFunction(sdk),

                        "func_create" to FuncCreateFunction(sdk),
                        "func_deploy" to FuncDeployFunction(sdk),
                        "func_deploy_latest" to FuncDeployLatestFunction(sdk),
                        "func_get" to FuncGetFunction(sdk),
                        "func_list" to FuncListFunction(sdk),
                        "func_invoke" to FuncInvokeFunction(sdk),
                        "func_update" to FuncUpdateFunction(sdk),

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