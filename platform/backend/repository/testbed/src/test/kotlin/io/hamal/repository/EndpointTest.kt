package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.EndpointCmdRepository.UpdateCmd
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class EndpointRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateEndpointTest {
        @TestFactory
        fun `Creates Endpoint`() = runWith(EndpointRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    endpointId = EndpointId(123),
                    groupId = GroupId(1),
                    flowId = FlowId(234),
                    name = EndpointName("SomeEndpoint"),
                    funcId = FuncId(345),
                )
            )

            with(result) {
                assertThat(id, equalTo(EndpointId(123)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(flowId, equalTo(FlowId(234)))
                assertThat(name, equalTo(EndpointName("SomeEndpoint")))
                assertThat(funcId, equalTo(FuncId(345)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exists in flow`() =
            runWith(EndpointRepository::class) {

                createEndpoint(
                    endpointId = EndpointId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = EndpointName("first-endpoint-name"),
                    funcId = FuncId(4)
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            endpointId = EndpointId(4),
                            groupId = GroupId(3),
                            flowId = FlowId(2),
                            name = EndpointName("first-endpoint-name"),
                            funcId = FuncId(345)
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("EndpointName(first-endpoint-name) already exists in namespace FlowId(2)")
                )

                verifyCount(1)
            }

        @TestFactory
        fun `Creates with same name but different flow`() =
            runWith(EndpointRepository::class) {

                createEndpoint(
                    endpointId = EndpointId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = EndpointName("endpoint-name"),
                    funcId = FuncId(4)
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(2),
                        endpointId = EndpointId(4),
                        groupId = GroupId(3),
                        flowId = FlowId(22),
                        name = EndpointName("endpoint-name"),
                        funcId = FuncId(345)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(EndpointId(4)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(22)))
                    assertThat(name, equalTo(EndpointName("endpoint-name")))
                    assertThat(funcId, equalTo(FuncId(345)))
                }

                verifyCount(2)
            }

        @TestFactory
        fun `Tries to create but cmd with endpoint id was already applied`() =
            runWith(EndpointRepository::class) {

                createEndpoint(
                    cmdId = CmdId(23456),
                    endpointId = EndpointId(5),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = EndpointName("first-endpoint-name"),
                    funcId = FuncId(4)
                )

                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        endpointId = EndpointId(5),
                        groupId = GroupId(333),
                        flowId = FlowId(2222),
                        name = EndpointName("second-endpoint-name"),
                        funcId = FuncId(444)
                    )
                )

                with(result) {
                    assertThat(id, equalTo(EndpointId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(name, equalTo(EndpointName("first-endpoint-name")))
                    assertThat(funcId, equalTo(FuncId(4)))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesEndpointTest {

        @TestFactory
        fun `Updates endpoint`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("endpoint-name"),
                funcId = FuncId(4)
            )

            val result = update(
                EndpointId(1), UpdateCmd(
                    id = CmdId(2),
                    name = EndpointName("Updated"),
                    funcId = FuncId(4444)
                )
            )

            with(result) {
                assertThat(id, equalTo(EndpointId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(EndpointName("Updated")))
                assertThat(funcId, equalTo(FuncId(4444)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates endpoint without updating it`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("endpoint-name"),
                funcId = FuncId(4)
            )

            val result = update(
                EndpointId(1), UpdateCmd(
                    id = CmdId(2),
                    name = null,
                    funcId = null
                )
            )

            with(result) {
                assertThat(id, equalTo(EndpointId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(EndpointName("endpoint-name")))
                assertThat(funcId, equalTo(FuncId(4)))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to update but same name already exists in flow`() =
            runWith(EndpointRepository::class) {

                createEndpoint(
                    endpointId = EndpointId(1),
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = EndpointName("already-exists"),
                    funcId = FuncId(4)
                )

                createEndpoint(
                    endpointId = EndpointId(2),
                    flowId = FlowId(2),
                    groupId = GroupId(21),
                    name = EndpointName("to-update"),
                    funcId = FuncId(22)
                )

                val exception = assertThrows<IllegalArgumentException> {
                    update(
                        EndpointId(2), UpdateCmd(
                            id = CmdId(2),
                            name = EndpointName("already-exists"),
                            funcId = FuncId(1111)
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("EndpointName(already-exists) already exists in namespace FlowId(2)")
                )

                with(get(EndpointId(2))) {
                    assertThat(id, equalTo(EndpointId(2)))
                    assertThat(flowId, equalTo(FlowId(2)))
                    assertThat(groupId, equalTo(GroupId(21)))
                    assertThat(name, equalTo(EndpointName("to-update")))
                    assertThat(funcId, equalTo(FuncId(22)))
                }

                verifyCount(2)
            }

    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(EndpointRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(EndpointRepository::class) {

            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("already-exists"),
                funcId = FuncId(4)
            )

            createEndpoint(
                endpointId = EndpointId(2),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("to-update"),
                funcId = FuncId(5)
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get endpoint by id`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("SomeEndpoint"),
                funcId = FuncId(4)
            )

            with(get(EndpointId(1))) {
                assertThat(id, equalTo(EndpointId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(EndpointName("SomeEndpoint")))
                assertThat(funcId, equalTo(FuncId(4)))
            }
        }

        @TestFactory
        fun `Tries to get endpoint by id but does not exist`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("SomeEndpoint"),
                funcId = FuncId(4)
            )

            val exception = assertThrows<NoSuchElementException> {
                get(EndpointId(111111))
            }
            assertThat(exception.message, equalTo("Endpoint not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find endpoint by id`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("SomeEndpoint"),
                funcId = FuncId(4)
            )

            with(find(EndpointId(1))!!) {
                assertThat(id, equalTo(EndpointId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(flowId, equalTo(FlowId(2)))
                assertThat(name, equalTo(EndpointName("SomeEndpoint")))
                assertThat(funcId, equalTo(FuncId(4)))
            }
        }

        @TestFactory
        fun `Tries to find endpoint by id but does not exist`() = runWith(EndpointRepository::class) {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("SomeEndpoint"),
                funcId = FuncId(4)
            )

            val result = find(EndpointId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(EndpointRepository::class) {
            setup()

            val result = list(listOf(EndpointId(111111), EndpointId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(EndpointId(3)))
                assertThat(flowId, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(EndpointName("Endpoint")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(EndpointRepository::class) {
            setup()

            val query = EndpointQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(EndpointId(4)))
                assertThat(flowId, equalTo(FlowId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(EndpointName("Endpoint")))
            }

            with(result[1]) {
                assertThat(id, equalTo(EndpointId(3)))
                assertThat(flowId, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(EndpointName("Endpoint")))
            }
        }

        @TestFactory
        fun `With flow ids`() = runWith(EndpointRepository::class) {
            setup()

            val query = EndpointQuery(
                flowIds = listOf(FlowId(10), FlowId(23)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(EndpointId(4)))
                assertThat(flowId, equalTo(FlowId(10)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(EndpointName("Endpoint")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(EndpointRepository::class) {
            setup()

            val query = EndpointQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(EndpointRepository::class) {
            setup()

            val query = EndpointQuery(
                afterId = EndpointId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(EndpointId(1)))
            }
        }

        private fun EndpointRepository.setup() {
            createEndpoint(
                endpointId = EndpointId(1),
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = EndpointName("Endpoint"),
                funcId = FuncId(4)
            )

            createEndpoint(
                endpointId = EndpointId(2),
                flowId = FlowId(3),
                groupId = GroupId(3),
                name = EndpointName("Endpoint"),
                funcId = FuncId(4)
            )

            createEndpoint(
                endpointId = EndpointId(3),
                flowId = FlowId(4),
                groupId = GroupId(4),
                name = EndpointName("Endpoint"),
                funcId = FuncId(6)
            )

            createEndpoint(
                endpointId = EndpointId(4),
                flowId = FlowId(10),
                groupId = GroupId(5),
                name = EndpointName("Endpoint"),
                funcId = FuncId(7)
            )
        }
    }
}

private fun EndpointRepository.createEndpoint(
    endpointId: EndpointId,
    flowId: FlowId,
    name: EndpointName,
    groupId: GroupId,
    funcId: FuncId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            endpointId = endpointId,
            groupId = groupId,
            flowId = flowId,
            name = name,
            funcId = funcId
        )
    )
}

private fun EndpointRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun EndpointRepository.verifyCount(expected: Int, block: EndpointQuery.() -> Unit) {
    val counted = count(EndpointQuery(groupIds = listOf()).also(block))
    assertThat("number of endpoints expected", counted, equalTo(Count(expected)))
}