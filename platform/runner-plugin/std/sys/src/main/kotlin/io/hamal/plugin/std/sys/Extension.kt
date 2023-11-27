package io.hamal.plugin.std.sys

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.plugin.std.sys.adhoc.AdhocFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintCreateFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintGetFunction
import io.hamal.plugin.std.sys.blueprint.BlueprintUpdateFunction
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
import io.hamal.plugin.std.sys.flow.FlowCreateFunction
import io.hamal.plugin.std.sys.flow.FlowExecsFunction
import io.hamal.plugin.std.sys.flow.FlowGetFunction
import io.hamal.plugin.std.sys.flow.FlowListFunction
import io.hamal.plugin.std.sys.func.*
import io.hamal.plugin.std.sys.hook.HookCreateFunction
import io.hamal.plugin.std.sys.hook.HookGetFunction
import io.hamal.plugin.std.sys.hook.HookListFunction
import io.hamal.plugin.std.sys.req.ReqGetFunction
import io.hamal.plugin.std.sys.topic.*
import io.hamal.plugin.std.sys.topic.trigger.*


class SysPluginFactory(
    private val sdk: ApiSdkImpl
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
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

                "endpoint_create" to EndpointCreateFunction(sdk),
                "endpoint_get" to EndpointGetFunction(sdk),
                "endpoint_list" to EndpointListFunction(sdk),

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

                "flow_create" to FlowCreateFunction(sdk),
                "flow_get" to FlowGetFunction(sdk),
                "flow_list" to FlowListFunction(sdk),
                "flow_execs" to FlowExecsFunction(sdk),

                "topic_create" to TopicCreateFunction(sdk),
                "topic_resolve" to TopicResolveFunction(sdk),
                "topic_list" to TopicListFunction(sdk),
                "topic_get" to TopicGetFunction(sdk),
                "topic_entry_append" to TopicEntryAppendFunction(sdk),
                "topic_entry_list" to TopicEntryListFunction(sdk),

                "trigger_create" to TriggerCreateFunction(sdk),
                "trigger_get" to TriggerGetFunction(sdk),
                "trigger_list" to TriggerListFunction(sdk),
                "trigger_activate" to TriggerActivateFunction(sdk),
                "trigger_deactivate" to TriggerDeactivateFunction(sdk),
            )
        )
    }
}