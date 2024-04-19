package io.hamal.plugin.std.sys

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.std.sys.adhoc.AdhocFunction
import io.hamal.plugin.std.sys.code.CodeGetFunction
import io.hamal.plugin.std.sys.endpoint.EndpointCreateFunction
import io.hamal.plugin.std.sys.endpoint.EndpointGetFunction
import io.hamal.plugin.std.sys.endpoint.EndpointListFunction
import io.hamal.plugin.std.sys.exec.ExecGetFunction
import io.hamal.plugin.std.sys.exec.ExecListFunction
import io.hamal.plugin.std.sys.extension.ExtensionCreateFunction
import io.hamal.plugin.std.sys.extension.ExtensionGetFunction
import io.hamal.plugin.std.sys.extension.ExtensionListFunction
import io.hamal.plugin.std.sys.extension.ExtensionUpdateFunction
import io.hamal.plugin.std.sys.func.*
import io.hamal.plugin.std.sys.hook.HookCreateFunction
import io.hamal.plugin.std.sys.hook.HookGetFunction
import io.hamal.plugin.std.sys.hook.HookListFunction
import io.hamal.plugin.std.sys.namespace.NamespaceAppendFunction
import io.hamal.plugin.std.sys.namespace.NamespaceGetFunction
import io.hamal.plugin.std.sys.namespace.NamespaceListFunction
import io.hamal.plugin.std.sys.namespace.NamespaceUpdateFunction
import io.hamal.plugin.std.sys.recipe.RecipeCreateFunction
import io.hamal.plugin.std.sys.recipe.RecipeGetFunction
import io.hamal.plugin.std.sys.recipe.RecipeUpdateFunction
import io.hamal.plugin.std.sys.request.RequestGetFunction
import io.hamal.plugin.std.sys.topic.*
import io.hamal.plugin.std.sys.trigger.*


class PluginSysFactory(
    private val sdk: ApiSdkImpl
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = KuaString("std.sys"),
            internals = mapOf(
                KuaString("await") to AwaitFunction(sdk.template),
                KuaString("await_completed") to AwaitCompletedFunction(sdk.template),
                KuaString("await_failed") to AwaitFailedFunction(sdk.template),

                KuaString("adhoc") to AdhocFunction(sdk),

                KuaString("recipe_create") to RecipeCreateFunction(sdk),
                KuaString("recipe_get") to RecipeGetFunction(sdk),
                KuaString("recipe_update") to RecipeUpdateFunction(sdk),

                KuaString("code_get") to CodeGetFunction(sdk),

                KuaString("req_get") to RequestGetFunction(sdk.template),

                KuaString("endpoint_create") to EndpointCreateFunction(sdk),
                KuaString("endpoint_get") to EndpointGetFunction(sdk),
                KuaString("endpoint_list") to EndpointListFunction(sdk),

                KuaString("exec_list") to ExecListFunction(sdk),
                KuaString("exec_get") to ExecGetFunction(sdk),

                KuaString("extension_create") to ExtensionCreateFunction(sdk),
                KuaString("extension_get") to ExtensionGetFunction(sdk),
                KuaString("extension_list") to ExtensionListFunction(sdk),
                KuaString("extension_update") to ExtensionUpdateFunction(sdk),

                KuaString("func_create") to FuncCreateFunction(sdk),
                KuaString("func_deploy") to FuncDeployFunction(sdk),
                KuaString("func_deployment_list") to FuncDeploymentsFunction(sdk),
                KuaString("func_get") to FuncGetFunction(sdk),
                KuaString("func_list") to FuncListFunction(sdk),
                KuaString("func_invoke") to FuncInvokeFunction(sdk),
                KuaString("func_update") to FuncUpdateFunction(sdk),

                KuaString("hook_create") to HookCreateFunction(sdk),
                KuaString("hook_get") to HookGetFunction(sdk),
                KuaString("hook_list") to HookListFunction(sdk),

                KuaString("namespace_append") to NamespaceAppendFunction(sdk),
                KuaString("namespace_get") to NamespaceGetFunction(sdk),
                KuaString("namespace_list") to NamespaceListFunction(sdk),
                KuaString("namespace_update") to NamespaceUpdateFunction(sdk),

                KuaString("topic_create") to TopicCreateFunction(sdk),
                KuaString("topic_resolve") to TopicResolveFunction(sdk),
                KuaString("topic_list") to TopicListFunction(sdk),
                KuaString("topic_get") to TopicGetFunction(sdk),
                KuaString("topic_entry_append") to TopicEntryAppendFunction(sdk),
                KuaString("topic_entry_list") to TopicEntryListFunction(sdk),

                KuaString("trigger_create") to TriggerCreateFunction(sdk),
                KuaString("trigger_get") to TriggerGetFunction(sdk),
                KuaString("trigger_list") to TriggerListFunction(sdk),
                KuaString("trigger_activate") to TriggerActivateFunction(sdk),
                KuaString("trigger_deactivate") to TriggerDeactivateFunction(sdk),
            )
        )
    }
}