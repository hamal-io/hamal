package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.fixture.GeneratorCapture
import io.hamal.lib.nodes.fixture.GeneratorInvoked
import io.hamal.lib.nodes.generator.GeneratorConstant
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.typesystem.value.ValueString


fun node(
    id: Long,
    type: String,
    controls: List<Control>,
    outputs: List<PortOutput> = listOf(),
    title: NodeTitle = NodeTitle("Title of ${id.toString(16)}"),
    position: Position = Position(0, 0),
    size: Size = Size(200, 200)
): Node {
    return Node(
        id = NodeId(SnowflakeId(id)),
        type = NodeType(type),
        title = title,
        position = position,
        size = size,
        controls = controls,
        outputs = outputs
    )
}

fun connection(
    id: Long,
    outputNode: Long,
    outputPort: Long,
    inputNode: Long,
    inputPort: Long
): Connection {
    return Connection(
        id = ConnectionId(SnowflakeId(id)),
        outputNode = Connection.Node(NodeId(SnowflakeId(outputNode))),
        outputPort = Connection.Port(id = PortId(SnowflakeId(outputPort))),
        inputNode = Connection.Node(NodeId(SnowflakeId(inputNode))),
        inputPort = Connection.Port(id = PortId(SnowflakeId(inputPort)))
    )
}

internal abstract class BaseNodesTest {

    class TestCaptor1 : Function1In0Out<KuaString>(FunctionInput1Schema(KuaString::class)) {
        override fun invoke(ctx: FunctionContext, arg1: KuaString) {
            result = ValueString(arg1.stringValue)
        }

        var result: ValueString? = null
    }

    class TestInvoked : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            invocations++
        }

        var invocations: Int = 0
    }

    fun run(graph: Graph) {
        val code = testCompiler.compile(graph)
        testInstance.codeLoad(code)
    }

    protected val testCompiler = Compiler(
        GeneratorRegistry(
            listOf(
                GeneratorConstant.String,
                GeneratorInvoked,
                GeneratorCapture.String
            )
        )
    )
    protected val testCaptor1 = TestCaptor1()
    protected val testInvoked = TestInvoked()

    protected val testInstance = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop).also { sandbox ->
            sandbox.register(
                RunnerPlugin(
                    name = KuaString("test"),
                    factoryCode = KuaCode(
                        """
                    function plugin_create(internal)
                        local export = {
                            invoked = internal.invoked,
                            capture1 =  internal.capture1
                        }
                        return export
                    end
                """.trimIndent()
                    ),
                    internals = mapOf(
                        KuaString("invoked") to testInvoked,
                        KuaString("capture1") to testCaptor1
                    )
                )
            )
        }
    }
}