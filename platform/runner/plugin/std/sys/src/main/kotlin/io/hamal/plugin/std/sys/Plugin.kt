package io.hamal.plugin.std.sys

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.common.value.ValueString
import io.hamal.plugin.std.sys.adhoc.AdhocFunction
import io.hamal.plugin.std.sys.code.CodeGetFunction
import io.hamal.plugin.std.sys.exec.ExecGetFunction
import io.hamal.plugin.std.sys.exec.ExecListFunction
import io.hamal.plugin.std.sys.extension.ExtensionCreateFunction
import io.hamal.plugin.std.sys.extension.ExtensionGetFunction
import io.hamal.plugin.std.sys.extension.ExtensionListFunction
import io.hamal.plugin.std.sys.extension.ExtensionUpdateFunction
import io.hamal.plugin.std.sys.func.*
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
            name = ValueString("std.sys"),
            internals = mapOf(
                ValueString("await") to AwaitFunction(sdk.template),
                ValueString("await_completed") to AwaitCompletedFunction(sdk.template),
                ValueString("await_failed") to AwaitFailedFunction(sdk.template),

                ValueString("adhoc") to AdhocFunction(sdk),

                ValueString("recipe_create") to RecipeCreateFunction(sdk),
                ValueString("recipe_get") to RecipeGetFunction(sdk),
                ValueString("recipe_update") to RecipeUpdateFunction(sdk),

                ValueString("code_get") to CodeGetFunction(sdk),

                ValueString("req_get") to RequestGetFunction(sdk.template),

                ValueString("exec_list") to ExecListFunction(sdk),
                ValueString("exec_get") to ExecGetFunction(sdk),

                ValueString("extension_create") to ExtensionCreateFunction(sdk),
                ValueString("extension_get") to ExtensionGetFunction(sdk),
                ValueString("extension_list") to ExtensionListFunction(sdk),
                ValueString("extension_update") to ExtensionUpdateFunction(sdk),

                ValueString("func_create") to FuncCreateFunction(sdk),
                ValueString("func_deploy") to FuncDeployFunction(sdk),
                ValueString("func_deployment_list") to FuncDeploymentsFunction(sdk),
                ValueString("func_get") to FuncGetFunction(sdk),
                ValueString("func_list") to FuncListFunction(sdk),
                ValueString("func_invoke") to FuncInvokeFunction(sdk),
                ValueString("func_update") to FuncUpdateFunction(sdk),

                ValueString("namespace_append") to NamespaceAppendFunction(sdk),
                ValueString("namespace_get") to NamespaceGetFunction(sdk),
                ValueString("namespace_list") to NamespaceListFunction(sdk),
                ValueString("namespace_update") to NamespaceUpdateFunction(sdk),

                ValueString("topic_create") to TopicCreateFunction(sdk),
                ValueString("topic_resolve") to TopicResolveFunction(sdk),
                ValueString("topic_list") to TopicListFunction(sdk),
                ValueString("topic_get") to TopicGetFunction(sdk),
                ValueString("topic_entry_append") to TopicEntryAppendFunction(sdk),
                ValueString("topic_entry_list") to TopicEntryListFunction(sdk),

                ValueString("trigger_create") to TriggerCreateFunction(sdk),
                ValueString("trigger_get") to TriggerGetFunction(sdk),
                ValueString("trigger_list") to TriggerListFunction(sdk),
                ValueString("trigger_activate") to TriggerActivateFunction(sdk),
                ValueString("trigger_deactivate") to TriggerDeactivateFunction(sdk),
            )
        )
    }
}