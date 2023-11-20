package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import io.hamal.repository.api.FlowCmdRepository.UpdateCmd
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.FlowRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class FlowRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateFlowTest {
        @TestFactory
        fun `Creates Flow`() = runWith(FlowRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    flowId = FlowId(234),
                    groupId = GroupId(1),
                    name = FlowName("SomeFlow"),
                    inputs = FlowInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rockz")
                            )
                        )
                    ),

                    )
            )

            with(result) {
                assertThat(id, equalTo(FlowId(234)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(type, equalTo(FlowType.default))
                assertThat(name, equalTo(FlowName("SomeFlow")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates Flow with type`() = runWith(FlowRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    flowId = FlowId(234),
                    groupId = GroupId(1),
                    name = FlowName("SomeFlow"),
                    inputs = FlowInputs(
                        MapType(
                            mutableMapOf(
                                "hamal" to StringType("rockz")
                            )
                        )
                    ),
                    type = FlowType("SpecialFlowType")

                )
            )

            with(result) {
                assertThat(id, equalTo(FlowId(234)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(type, equalTo(FlowType("SpecialFlowType")))
                assertThat(name, equalTo(FlowName("SomeFlow")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exist`() =
            runWith(FlowRepository::class) {

                createFlow(
                    flowId = FlowId(2),
                    groupId = GroupId(3),
                    name = FlowName("first-flow-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            flowId = FlowId(4),
                            groupId = GroupId(3),
                            name = FlowName("first-flow-name"),
                            inputs = FlowInputs(),
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("FlowName(first-flow-name) already exists")
                )

                verifyCount(1)
            }


        @TestFactory
        fun `Tries to create but cmd with flow id was already applied`() =
            runWith(FlowRepository::class) {

                createFlow(
                    cmdId = CmdId(23456),
                    flowId = FlowId(5),
                    groupId = GroupId(3),
                    name = FlowName("first-flow-name")
                )


                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        flowId = FlowId(5),
                        groupId = GroupId(333),
                        name = FlowName("second-flow-name"),
                        inputs = FlowInputs(),
                    )
                )

                with(result) {
                    assertThat(id, equalTo(FlowId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(name, equalTo(FlowName("first-flow-name")))
                    assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesFlowTest {

        @TestFactory
        fun `Updates flow`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("flow-name")
            )

            val result = update(
                FlowId(1), UpdateCmd(
                    id = CmdId(2),
                    name = FlowName("Updated"),
                    inputs = FlowInputs(MapType(mutableMapOf("answer" to NumberType(42)))),
                )
            )

            with(result) {
                assertThat(id, equalTo(FlowId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(FlowName("Updated")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("answer" to NumberType(42))))))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates flow without updating it`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("flow-name")
            )

            val result = update(
                FlowId(1), UpdateCmd(
                    id = CmdId(2),
                    name = null,
                    inputs = null,
                )
            )

            with(result) {
                assertThat(id, equalTo(FlowId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(FlowName("flow-name")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(FlowRepository::class) {
            clear()
            verifyCount(0)
        }


        @TestFactory
        fun `Clear table`() = runWith(FlowRepository::class) {

            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("already-exists")
            )

            createFlow(
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = FlowName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get flow by id`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("SomeFlow")
            )

            with(get(FlowId(1))) {
                assertThat(id, equalTo(FlowId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(FlowName("SomeFlow")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            }
        }

        @TestFactory
        fun `Tries to get flow by id but does not exist`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("SomeFlow")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(FlowId(111111))
            }
            assertThat(exception.message, equalTo("Flow not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find flow by id`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("SomeFlow")
            )

            with(find(FlowId(1))!!) {
                assertThat(id, equalTo(FlowId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(FlowName("SomeFlow")))
                assertThat(inputs, equalTo(FlowInputs(MapType(mutableMapOf("hamal" to StringType("rockz"))))))
            }
        }

        @TestFactory
        fun `Tries to find flow by id but does not exist`() = runWith(FlowRepository::class) {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("SomeFlow")
            )

            val result = find(FlowId(111111))
            assertThat(result, Matchers.nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(FlowRepository::class) {
            setup()

            val result = list(listOf(FlowId(111111), FlowId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(FlowId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(FlowName("Flow-Three")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(FlowRepository::class) {
            setup()

            val query = FlowQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(FlowId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(FlowName("Flow-Four")))
            }

            with(result[1]) {
                assertThat(id, equalTo(FlowId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(FlowName("Flow-Three")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(FlowRepository::class) {
            setup()

            val query = FlowQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(FlowRepository::class) {
            setup()

            val query = FlowQuery(
                afterId = FlowId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(FlowId(1)))
            }
        }

        private fun FlowRepository.setup() {
            createFlow(
                flowId = FlowId(1),
                groupId = GroupId(3),
                name = FlowName("Flow-One")
            )

            createFlow(
                flowId = FlowId(2),
                groupId = GroupId(3),
                name = FlowName("Flow-Two")
            )

            createFlow(
                flowId = FlowId(3),
                groupId = GroupId(4),
                name = FlowName("Flow-Three")
            )

            createFlow(
                flowId = FlowId(4),
                groupId = GroupId(5),
                name = FlowName("Flow-Four")
            )
        }
    }
}

private fun FlowRepository.createFlow(
    flowId: FlowId,
    name: FlowName,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            flowId = flowId,
            groupId = groupId,
            name = name,
            inputs = FlowInputs(
                MapType(
                    mutableMapOf(
                        "hamal" to StringType("rockz")
                    )
                )
            )
        )
    )
}

private fun FlowRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun FlowRepository.verifyCount(expected: Int, block: FlowQuery.() -> Unit) {
    val counted = count(FlowQuery(groupIds = listOf()).also(block))
    assertThat("number of flows expected", counted, equalTo(expected.toULong()))
}