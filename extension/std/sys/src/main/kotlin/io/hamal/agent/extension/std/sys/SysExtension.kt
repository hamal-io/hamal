package io.hamal.agent.extension.std.sys

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.value.NamedFunctionValue
import io.hamal.lib.sdk.HttpTemplateSupplier

class SysExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) {
    fun create(): Extension {
        return Extension(
            name = "sys",
            functions = listOf(
                NamedFunctionValue(
                    name = "adhoc",
                    function = InvokeAdhocFunction(templateSupplier)
                )
            )
        )
    }
}