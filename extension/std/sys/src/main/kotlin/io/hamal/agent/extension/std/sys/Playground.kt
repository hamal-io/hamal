package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox


fun main() {
    NativeLoader.load(Resources)
    val template = HttpTemplate("http://localhost:8008")
    val sandbox = Sandbox()

    sandbox.register(SysExtensionFactory { template }.create())

    sandbox.runCode(
        """
            
        local table = {code = "print('hello')"}
        print(table)
            
        local result = sys.adhoc(table)
        
        local x = sys.list_execs()
        
        for k,v in pairs(x) do
            print(k,v)
        end
        
        print("id:", x[1].id)
        print("status:", x[1].status)

    """.trimIndent()
    )
}