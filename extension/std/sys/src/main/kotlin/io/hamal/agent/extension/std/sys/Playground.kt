package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox


fun main() {
    ResourceLoader.load()
    val template = HttpTemplate("http://localhost:8084")
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
        
        print(x[1])

    """.trimIndent()
    )
}