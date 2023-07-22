package io.hamal.agent.extension.std.sys

import io.hamal.agent.extension.api.ExtensionFactory
import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.value.NamedFunctionValue
import io.hamal.lib.sdk.HttpTemplateSupplier

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ExtensionFactory {

    override fun create(): Extension {
        return Extension(
            name = "sys",
            functions = listOf(
                NamedFunctionValue(
                    name = "adhoc",
                    function = InvokeAdhocFunction(templateSupplier)
                ),
                NamedFunctionValue(
                    name = "list_execs",
                    function = ListExecsFunction(templateSupplier)
                )
            )
        )
    }
}