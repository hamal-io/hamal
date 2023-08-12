package io.hamal.extension.std.sys

import io.hamal.extension.std.sys.exec.GetExecFunction
import io.hamal.extension.std.sys.exec.InvokeAdhocFunction
import io.hamal.extension.std.sys.exec.ListExecsFunction
import io.hamal.extension.std.sys.func.*
import io.hamal.extension.std.sys.namespace.CreateNamespaceFunction
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.sdk.HttpTemplateSupplier

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "sys",
            internals = mapOf(
                "adhoc" to InvokeAdhocFunction(templateSupplier),

                "list_execs" to ListExecsFunction(templateSupplier),
                "get_exec" to GetExecFunction(templateSupplier),

                "create_func" to CreateFuncFunction(templateSupplier),
                "get_func" to GetFuncFunction(templateSupplier),
                "list_func" to ListFuncsFunction(templateSupplier),

                "create_namespace" to CreateNamespaceFunction(templateSupplier),
                "get_namespace" to GetNamespaceFunction(templateSupplier),
                "list_namespace" to ListNamespacesFunction(templateSupplier)

            )
        )
    }
}