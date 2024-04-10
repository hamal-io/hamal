package io.hamal.lib.nodes.compiler

import io.hamal.lib.nodes.Graph

object Compiler {

    fun compile(graph: Graph): String {
        return """
            context = {}
            
            context.n1 = {
                inputs = {
                    [1] = { total_holder = 42 } 
                }
            }
            
            function n1() 
                return nil, context.n1.inputs[1]
            end


            function n2(args)
                if args.total_holder > 10 then
                    return nil, args
                end

                return nil, nil
            end

            function n3(args) 
                if args == nil then return nil, nil end
                print("A new token pair meets your criteria")
            end

            err_1, out_1 = n1()
            err_2, out_2 = n2(out_1)
            
            -- n3 does not accept null type
            err_3, out_3 = n3(out_2)
        """.trimIndent()
    }

}