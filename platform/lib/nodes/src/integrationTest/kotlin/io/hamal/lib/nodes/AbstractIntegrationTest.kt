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
import io.hamal.lib.nodes.control.ControlId
import io.hamal.lib.nodes.fixture.test_nodes.GeneratorCapture
import io.hamal.lib.nodes.fixture.test_nodes.GeneratorInvoked
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.nodes.generator.defaultGeneratorRegistry
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.value.*
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import io.hamal.runner.test.TestConnector


internal abstract class AbstractIntegrationTest {

    class TestCaptor1 : Function1In0Out<KuaType>(FunctionInput1Schema(KuaType::class)) {
        override fun invoke(ctx: FunctionContext, arg1: KuaType) {
            when (arg1) {
                is KuaBoolean -> resultBoolean = if (arg1.booleanValue) ValueTrue else ValueFalse
                is KuaDecimal -> resultDecimal = ValueDecimal(arg1.value)
                is KuaNumber -> resultNumber = ValueNumber(arg1.doubleValue)
                is KuaString -> resultString = ValueString((arg1.stringValue))

                else -> TODO()
            }
        }

        var resultBoolean: ValueBoolean? = null
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
                                    GeneratorCapture.Boolean,
                                    GeneratorCapture.Decimal,
                                    GeneratorCapture.Number,
                                    GeneratorCapture.String,
                                    GeneratorInvoked
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

    fun node(
        id: Long,
        type: String,
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
            outputs = outputs
        )
    }

    fun connection(
        id: Long,
        outputNode: Long,
        outputPort: Long,
        inputNode: Long,
        inputPort: Long,
        label: String? = null
    ): Connection {
        return Connection(
            id = ConnectionId(SnowflakeId(id)),
            outputNode = Connection.Node(NodeId(SnowflakeId(outputNode))),
            outputPort = Connection.Port(id = PortId(SnowflakeId(outputPort))),
            inputNode = Connection.Node(NodeId(SnowflakeId(inputNode))),
            inputPort = Connection.Port(id = PortId(SnowflakeId(inputPort))),
            label = label?.let(::ConnectionLabel)
        )
    }

    fun portInput(
        id: Long,
        type: Type
    ): PortInput = PortInput(PortId(SnowflakeId(id)), type)

    fun portOutput(
        id: Long,
        type: Type
    ): PortOutput = PortOutput(PortId(SnowflakeId(id)), type)

    fun unitOfWork(
        initValue: HotNode<*>,
        graph: NodesGraph
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

    protected val nextControlId = NextControlId

    object NextControlId {

        operator fun invoke(): ControlId {
            return ControlId(counter++)
        }

        private var counter: Int = 0
    }
}