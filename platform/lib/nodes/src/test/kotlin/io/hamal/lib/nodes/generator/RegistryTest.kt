package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.TypeString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GeneratorRegistryTest {

    @Test
    fun `get - Gets generator without input types`() {
        val result = testInstance[NodeType("ANOTHER_TYPE"), listOf()]
        assertThat(result, equalTo(anotherGenerator))
    }

    @Test
    fun `get - Gets generator by different input types`() {
        val result = testInstance[NodeType("SOME_TYPE"), listOf(TypeNumber)]
        assertThat(result, equalTo(someGeneratorTwo))
    }

    @Test
    fun `get - Tries to get but node type not registered`() {
        assertThrows<NoSuchElementException> {
            testInstance[NodeType("DOES_NOT_EXISTS"), listOf()]
        }.also { exception -> assertThat(exception.message, equalTo("No generator found for NodeType(DOES_NOT_EXISTS) with []")) }
    }

    @Test
    fun `get - Tries to get but input type not supported`() {
        assertThrows<NoSuchElementException> {
            testInstance[NodeType("ANOTHER_TYPE"), listOf(TypeString)]
        }.also { exception -> assertThat(exception.message, equalTo("No generator found for NodeType(ANOTHER_TYPE) with [TypeString]")) }
    }

    private val someGeneratorOne = object : Generator {
        override val type: NodeType get() = NodeType("SOME_TYPE")
        override val inputTypes: List<TypeNew> = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = TODO("Not yet implemented")
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }
    private val someGeneratorTwo = object : Generator {
        override val type: NodeType get() = NodeType("SOME_TYPE")
        override val inputTypes: List<TypeNew> = listOf(TypeNumber)
        override val outputTypes: List<TypeNew> get() = TODO("Not yet implemented")
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val anotherGenerator = object : Generator {
        override val type: NodeType get() = NodeType("ANOTHER_TYPE")
        override val inputTypes: List<TypeNew> = listOf()
        override val outputTypes: List<TypeNew> get() = TODO("Not yet implemented")
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }


    private val testInstance = GeneratorRegistry(
        listOf(
            someGeneratorOne,
            someGeneratorTwo,
            anotherGenerator
        )
    )

}