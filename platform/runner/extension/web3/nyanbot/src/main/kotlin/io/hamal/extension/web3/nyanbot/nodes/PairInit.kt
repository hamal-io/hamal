//package io.hamal.extension.web3.nyanbot.nodes
//
//import io.hamal.lib.common.value.TypeObject
//import io.hamal.lib.common.value.ValueCode
//import io.hamal.lib.common.value.ValueType
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//import io.hamal.lib.nodes.compiler.node.NodeCompiler
//import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context
//
//
//internal data object PairInit : NodeCompiler() {
//    override val type = NodeType("Nyanbot_Pair_Init")
//    override val inputTypes = emptyList<ValueType>()
//    override val outputTypes: List<ValueType> = listOf(TypeObject)
//
//    override fun toCode(ctx: Context): ValueCode {
//        val config = ctx.controls.filterIsInstance<ControlInit>().firstOrNull()?.config
//            ?: throw IllegalArgumentException("Config not found")
//        println(config)
//
//        return ValueCode(
//            """
//                throw = require('std.throw').create()
//                table = require('std.table').create()
//
//                evt = context.exec.inputs.event or {}
//                address = evt.address or throw.illegal_argument('address not found')
//
//                print(address)
//
//                context.state.completed_addresses = context.state.completed_addresses or { }
//
//                for k,v in ipairs(context ) do print(k,v) end
//
//                if table.contains(context.state.completed_addresses, address) then
//                   print('done already')
//                  context.complete({})
//                end
//
//                -- context.on_complete(function()
//                --   table.insert(context.state.completed_addresses, address)
//                -- end)
//
//                -- table.insert(context.state.completed_addresses, address)
//
//                print(dump(context.state.completed_addresses))
//
//
//                http = req
//
//                http = require('net.http').create({})
//                resp = fail_on_error(http.post({
//                    url ='http://localhost:8008/v1/endpoints/f3f08c89810001',
//                    body = {
//                        address = address
//                    }
//                }))
//                print(resp.content)
//
//                for k,v in pairs(resp.content.result) do print(k,v) end
//
//                return resp.content.result
//            """.trimIndent()
//        )
//    }
//
//}
//
