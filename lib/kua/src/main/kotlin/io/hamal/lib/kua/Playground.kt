package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.value.StringValue


fun main() {
    NativeLoader.load(BuildDir)

    Sandbox().use { sb ->
        val config = ExtensionConfig(
            mutableMapOf(
                "host" to StringValue("http://test")
            )
        )

        val ext = Extension(
            name = "ctx",
            extensions = listOf(),
            config = config,
            functions = listOf(
                NamedFunctionValue(
                    name = "get_config",
                    function = ExtensionGetConfigFunction(config)
                ),
                NamedFunctionValue(
                    name = "update_config",
                    function = ExtensionUpdateConfigFunction(config)
                ),
                NamedFunctionValue(
                    name = "invoke",
                    function = object : Function0In0Out() {
                        override fun invoke(ctx: FunctionContext) {
                            println("calling host: ${config.value["host"]}")
                        }
                    }
                )
            )
        )

        sb.registerGlobalExtension(ext)

        sb.runCode(
            """
            ctx.update_config({
                host = 'http//localhost:8000',
            })
            
            local cfg = ctx.get_config()
                
            print(cfg)
            for k,v in pairs(cfg) do
                print(k.."=",v)
            end
            
            ctx.invoke()
            
        """.trimIndent()
        )

    }
}

