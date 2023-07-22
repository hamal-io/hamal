package io.hamal.bootstrap

import io.hamal.agent.extension.std.log.LogExtensionFactory
import io.hamal.lib.kua.FixedPathLoader
import io.hamal.lib.kua.Sandbox

fun main(){
    FixedPathLoader.load()
    val result = Sandbox()
    result.register(TestExtensionFactory().create())
    result.register(LogExtensionFactory().create())

    result.runCode("""
        assert(1==1)
                
        for k,v in pairs(log)do
            print(k,v)
        end
        
        log.info("Test")
    """.trimIndent())


}