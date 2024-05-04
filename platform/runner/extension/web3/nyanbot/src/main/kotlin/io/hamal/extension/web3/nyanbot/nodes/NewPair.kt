package io.hamal.extension.web3.nyanbot.nodes

import io.hamal.lib.common.value.TypeObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.ControlInit
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context


internal data object NewPair : NodeCompiler {
    override val type = NodeType("Nyanbot_New_Pair")
    override val inputTypes = emptyList<ValueType>()
    override val outputTypes: List<ValueType> = listOf(TypeObject)

    override fun toCode(ctx: Context): ValueCode {
        val config = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config ?: throw IllegalArgumentException("Config not found")
        println(config)

        return ValueCode(
            """
                address = context.exec.inputs.event.address
                
                print(address)
                
                http = req
                
                http = require('net.http').create({})
                resp = fail_on_error(http.post({url ='http://localhost:8008/v1/endpoints/f3f08c89810001'}))
                print(resp.content)

                for k,v in pairs(resp.content.result) do print(k,v) end
                
                decimals = require('std.decimal')
                return resp.content.result
            """.trimIndent()
        )
    }

}

