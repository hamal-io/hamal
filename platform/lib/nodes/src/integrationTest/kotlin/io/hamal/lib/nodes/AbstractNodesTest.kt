package io.hamal.lib.nodes

import io.hamal.extension.std.decimal.ExtensionDecimalFactory
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.nodes.fixture.test_nodes.GeneratorCapture
import io.hamal.lib.nodes.fixture.test_nodes.GeneratorInvoked
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.nodes.generator.defaultGeneratorRegistry
import io.hamal.lib.typesystem.value.ValueDecimal
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueString
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import io.hamal.runner.test.TestConnector


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

internal abstract class AbstractNodesTest {

    class TestCaptor1 : Function1In0Out<KuaType>(FunctionInput1Schema(KuaType::class)) {
        override fun invoke(ctx: FunctionContext, arg1: KuaType) {
            when (arg1) {
                is KuaDecimal -> resultDecimal = ValueDecimal(arg1.value)
                is KuaNumber -> resultNumber = ValueNumber(arg1.doubleValue)
                is KuaString -> resultString = ValueString((arg1.stringValue))

                else -> TODO()
            }
        }

        var resultDecimal: ValueDecimal? = null
        var resultNumber: ValueNumber? = null
        var resultString: ValueString? = null
    }

    class TestInvoked : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            invocations++
        }

        var invocations: Int = 0
    }

    fun createTestRunner(connector: Connector = TestConnector()) = CodeRunnerImpl(
        connector,
        object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(SandboxContextNop).also { sandbox ->
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
                }.also { sandbox -> sandbox.registerExtensions(ExtensionDecimalFactory) }
                    .also { sandbox -> sandbox.generatorRegistry.register(defaultGeneratorRegistry) }
                    .also { sandbox ->
                        sandbox.generatorRegistry.register(
                            GeneratorRegistry(
                                listOf(
                                    GeneratorInvoked,
                                    GeneratorCapture.Decimal,
                                    GeneratorCapture.Number,
                                    GeneratorCapture.String,
                                )
                            )
                        )
                    }
            }
        },
        object : EnvFactory {
            override fun create() = RunnerEnv()
        }
    )

    fun unitOfWork(
        initValue: HotNode<*>,
        graph: Graph
    ) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(
            HotObject.builder()
                .set("__nodes__init__", initValue)
                .build()
        ),
        state = State(),
        code = CodeValue(json.serialize(graph)),
        codeType = CodeType.Nodes,
        correlation = null
    )


    protected val testCaptor1 = TestCaptor1()
    protected val testInvoked = TestInvoked()
}