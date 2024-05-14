package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes.Nodes
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.nodes.ConnectionId.Companion.ConnectionId
import io.hamal.lib.nodes.ConnectionLabel.Companion.ConnectionLabel
import io.hamal.lib.nodes.ControlIdentifier.Companion.ControlIdentifier
import io.hamal.lib.nodes.NodeId.Companion.NodeId
import io.hamal.lib.nodes.NodeTitle.Companion.NodeTitle
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.PortId.Companion.PortId
import io.hamal.lib.nodes.compiler.node.Filter
import io.hamal.lib.nodes.compiler.node.Input
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry
import io.hamal.lib.nodes.compiler.node.defaultNodeCompilerRegistry
import io.hamal.lib.nodes.fixture.Capture
import io.hamal.lib.nodes.fixture.CaptureFunction
import io.hamal.lib.nodes.fixture.InvokeFunction
import io.hamal.lib.nodes.fixture.Invoked
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractIntegrationTest {

    data class TestContext(
        val captorOne: CaptureFunction = CaptureFunction(),
        val captorTwo: CaptureFunction = CaptureFunction(),
        val invokedOne: InvokeFunction = InvokeFunction(),
        val invokedTwo: InvokeFunction = InvokeFunction()
    )

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            pluginFactories = listOf(
                RunnerPluginFactory {
                    RunnerPlugin(
                        name = ValueString("test"),
                        factoryCode = ValueCode(
                            """
                    function plugin_create(internal)
                        local export = {
                            invoke_one = internal.invoke_one,
                            invoke_two = internal.invoke_two,
                            capture_one =  internal.capture_one,
                            capture_two =  internal.capture_two
                        }
                        return export
                    end
                """.trimIndent()
                        ),
                        internals = mapOf(
                            ValueString("invoke_one") to testContext.invokedOne,
                            ValueString("invoke_two") to testContext.invokedTwo,
                            ValueString("capture_one") to testContext.captorOne,
                            ValueString("capture_two") to testContext.captorTwo
                        )
                    )
                }
            ),
            nodeCompilerRegistries = listOf(
                defaultNodeCompilerRegistry,
                NodeCompilerRegistry(
                    listOf(
                        Capture.Boolean,
                        Capture.Decimal,
                        Capture.Number,
                        Capture.String,
                        Invoked.Boolean,
                        Invoked.Empty,
                        Invoked.String,
                    )
                )
            )
        ).run(unitOfWork)
    }

    fun node(
        id: Long,
        type: String,
        outputs: List<PortOutput> = listOf(),
        properties: ValueObject = ValueObject.empty,
        title: NodeTitle = NodeTitle("Title of ${id.toString(16)}"),
        position: Position = Position(0.0, 0.0),
        size: Size = Size(200, 200)
    ): Node {
        return Node(
            id = NodeId(SnowflakeId(id)),
            type = NodeType(type),
            title = title,
            position = position,
            size = size,
            properties = NodeProperties(properties),
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
        type: ValueType
    ): PortInput = PortInput(PortId(SnowflakeId(id)), type)

    fun portOutput(
        id: Long,
        type: ValueType
    ): PortOutput = PortOutput(PortId(SnowflakeId(id)), type)

    fun unitOfWork(
        graph: NodesGraph,
        initValue: ValueSerializable = ValueNil,
    ) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        triggerId = TriggerId(4567),
        inputs = ExecInputs(
            ValueObject.builder()
                .set("__nodes__init__", initValue)
                .build()
        ),
        state = State(),
        code = CodeValue(serde.write(graph)),
        codeType = CodeType(Nodes),
        correlation = null
    )


    protected val nextControlIdentifier = NextControlIdentifier
    protected val testContext = TestContext()

    object NextControlIdentifier {

        operator fun invoke(): ControlIdentifier {
            return ControlIdentifier((counter++).toString(16))
        }

        private var counter: Int = 0
    }
}