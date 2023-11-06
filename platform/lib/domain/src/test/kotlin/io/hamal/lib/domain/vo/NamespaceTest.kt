package io.hamal.lib.domain.vo

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class AllNamespaceNamesTest {

    @Test
    fun `Single name`() {
        val result = NamespaceName("io").allNamespaceNames()
        assertThat(
            result, equalTo(
                listOf(NamespaceName("io"))
            )
        )
    }

    @Test
    fun `One level`() {
        val result = NamespaceName("io::hamal").allNamespaceNames()
        assertThat(
            result, equalTo(
                listOf(
                    NamespaceName("io"),
                    NamespaceName("io::hamal")
                )
            )
        )
    }

    @Test
    fun `Two level`() {
        val result = NamespaceName("io::hamal::web3").allNamespaceNames()
        assertThat(
            result, equalTo(
                listOf(
                    NamespaceName("io"),
                    NamespaceName("io::hamal"),
                    NamespaceName("io::hamal::web3"),
                )
            )
        )
    }

    @Test
    fun `Three level`() {
        val result = NamespaceName("io::hamal::web3::eth").allNamespaceNames()
        assertThat(
            result, equalTo(
                listOf(
                    NamespaceName("io"),
                    NamespaceName("io::hamal"),
                    NamespaceName("io::hamal::web3"),
                    NamespaceName("io::hamal::web3::eth"),
                )
            )
        )
    }
}