package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceCmdRepository.UpdateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
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

internal class NamespaceRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class CreateNamespaceTest {
        @TestFactory
        fun `Creates Namespace`() = runWith(NamespaceRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    namespaceId = NamespaceId(234),
                    groupId = GroupId(1),
                    name = NamespaceName("SomeNamespace"),
                )
            )

            with(result) {
                assertThat(id, equalTo(NamespaceId(234)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(name, equalTo(NamespaceName("SomeNamespace")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Creates Namespace with type`() = runWith(NamespaceRepository::class) {
            val result = create(
                CreateCmd(
                    id = CmdId(1),
                    namespaceId = NamespaceId(234),
                    groupId = GroupId(1),
                    name = NamespaceName("SomeNamespace"),

                    )
            )

            with(result) {
                assertThat(id, equalTo(NamespaceId(234)))
                assertThat(groupId, equalTo(GroupId(1)))
                assertThat(name, equalTo(NamespaceName("SomeNamespace")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to create but same name already exist`() =
            runWith(NamespaceRepository::class) {

                createNamespace(
                    namespaceId = NamespaceId(2),
                    groupId = GroupId(3),
                    name = NamespaceName("first-namespace-name")
                )

                val exception = assertThrows<IllegalArgumentException> {
                    create(
                        CreateCmd(
                            id = CmdId(2),
                            namespaceId = NamespaceId(4),
                            groupId = GroupId(3),
                            name = NamespaceName("first-namespace-name")
                        )
                    )
                }

                assertThat(
                    exception.message,
                    equalTo("NamespaceName(first-namespace-name) already exists")
                )

                verifyCount(1)
            }


        @TestFactory
        fun `Tries to create but cmd with namespace id was already applied`() =
            runWith(NamespaceRepository::class) {

                createNamespace(
                    cmdId = CmdId(23456),
                    namespaceId = NamespaceId(5),
                    groupId = GroupId(3),
                    name = NamespaceName("first-namespace-name")
                )


                val result = create(
                    CreateCmd(
                        id = CmdId(23456),
                        namespaceId = NamespaceId(5),
                        groupId = GroupId(333),
                        name = NamespaceName("second-namespace-name")
                    )
                )

                with(result) {
                    assertThat(id, equalTo(NamespaceId(5)))
                    assertThat(groupId, equalTo(GroupId(3)))
                    assertThat(name, equalTo(NamespaceName("first-namespace-name")))
                }

                verifyCount(1)
            }
    }

    @Nested
    inner class UpdatesNamespaceTest {

        @TestFactory
        fun `Updates namespace`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("namespace-name")
            )

            val result = update(
                NamespaceId(1), UpdateCmd(
                    id = CmdId(2),
                    name = NamespaceName("Updated")
                )
            )

            with(result) {
                assertThat(id, equalTo(NamespaceId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(NamespaceName("Updated")))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Updates namespace without updating it`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("namespace-name")
            )

            val result = update(
                NamespaceId(1), UpdateCmd(
                    id = CmdId(2),
                    name = null
                )
            )

            with(result) {
                assertThat(id, equalTo(NamespaceId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(NamespaceName("namespace-name")))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(NamespaceRepository::class) {
            clear()
            verifyCount(0)
        }


        @TestFactory
        fun `Clear table`() = runWith(NamespaceRepository::class) {

            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("already-exists")
            )

            createNamespace(
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = NamespaceName("to-update")
            )

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get namespace by id`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("SomeNamespace")
            )

            with(get(NamespaceId(1))) {
                assertThat(id, equalTo(NamespaceId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(NamespaceName("SomeNamespace")))
            }
        }

        @TestFactory
        fun `Tries to get namespace by id but does not exist`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("SomeNamespace")
            )

            val exception = assertThrows<NoSuchElementException> {
                get(NamespaceId(111111))
            }
            assertThat(exception.message, equalTo("Namespace not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find namespace by id`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("SomeNamespace")
            )

            with(find(NamespaceId(1))!!) {
                assertThat(id, equalTo(NamespaceId(1)))
                assertThat(groupId, equalTo(GroupId(3)))
                assertThat(name, equalTo(NamespaceName("SomeNamespace")))
            }
        }

        @TestFactory
        fun `Tries to find namespace by id but does not exist`() = runWith(NamespaceRepository::class) {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("SomeNamespace")
            )

            val result = find(NamespaceId(111111))
            assertThat(result, Matchers.nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(NamespaceRepository::class) {
            setup()

            val result = list(listOf(NamespaceId(111111), NamespaceId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(NamespaceId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(NamespaceName("Namespace-Three")))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(NamespaceRepository::class) {
            setup()

            val query = NamespaceQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(NamespaceId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
                assertThat(name, equalTo(NamespaceName("Namespace-Four")))
            }

            with(result[1]) {
                assertThat(id, equalTo(NamespaceId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
                assertThat(name, equalTo(NamespaceName("Namespace-Three")))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(NamespaceRepository::class) {
            setup()

            val query = NamespaceQuery(
                groupIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(NamespaceRepository::class) {
            setup()

            val query = NamespaceQuery(
                afterId = NamespaceId(2),
                groupIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(NamespaceId(1)))
            }
        }

        private fun NamespaceRepository.setup() {
            createNamespace(
                namespaceId = NamespaceId(1),
                groupId = GroupId(3),
                name = NamespaceName("Namespace-One")
            )

            createNamespace(
                namespaceId = NamespaceId(2),
                groupId = GroupId(3),
                name = NamespaceName("Namespace-Two")
            )

            createNamespace(
                namespaceId = NamespaceId(3),
                groupId = GroupId(4),
                name = NamespaceName("Namespace-Three")
            )

            createNamespace(
                namespaceId = NamespaceId(4),
                groupId = GroupId(5),
                name = NamespaceName("Namespace-Four")
            )
        }
    }
}

private fun NamespaceRepository.createNamespace(
    namespaceId: NamespaceId,
    name: NamespaceName,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) {
    create(
        CreateCmd(
            id = cmdId,
            namespaceId = namespaceId,
            groupId = groupId,
            name = name
        )
    )
}

private fun NamespaceRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun NamespaceRepository.verifyCount(expected: Int, block: NamespaceQuery.() -> Unit) {
    val counted = count(NamespaceQuery(groupIds = listOf()).also(block))
    assertThat("number of namespaces expected", counted, equalTo(Count(expected)))
}