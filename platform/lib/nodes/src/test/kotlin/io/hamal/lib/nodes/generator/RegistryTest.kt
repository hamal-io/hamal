package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeDecimal
import io.hamal.lib.typesystem.TypeNew
import io.hamal.lib.typesystem.TypeNumber
import io.hamal.lib.typesystem.TypeString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GeneratorRegistryTest {

    @Test
    fun `get - Gets generator with no input and output types`() {
        val result = testInstance[NodeType("NO_TYPE"), listOf(), listOf()]
        assertThat(result, equalTo(noTypeGenerator))
    }

    @Test
    fun `get - Gets pass through generator`() {
        val result = testInstance[NodeType("STRING_PASS_THROUGH"), listOf(TypeString), listOf(TypeString)]
        assertThat(result, equalTo(stringPassThroughGenerator))
    }

    @Test
    fun `get - Gets generator with input types and not output types`() {
        val result = testInstance[NodeType("SOME_TYPE"), listOf(TypeNumber), listOf()]
        assertThat(result, equalTo(someGeneratorTwo))
    }

    @Test
    fun `get - Gets generator with output types and not input types`() {
        val result = testInstance[NodeType("ANOTHER_TYPE"), listOf(), listOf(TypeDecimal)]
        assertThat(result, equalTo(anotherGeneratorTwo))
    }

    @Test
    fun `get - Tries to get but node type not registered`() {
        assertThrows<NoSuchElementException> {
            testInstance[NodeType("DOES_NOT_EXISTS"), listOf(), listOf()]
        }.also { exception -> assertThat(exception.message, equalTo("No generator found for NodeType(DOES_NOT_EXISTS) with [] and []")) }
    }

    @Test
    fun `get - Tries to get but input type not supported`() {
        assertThrows<NoSuchElementException> {
            testInstance[NodeType("ANOTHER_TYPE"), listOf(TypeNumber), listOf()]
        }.also { exception -> assertThat(exception.message, equalTo("No generator found for NodeType(ANOTHER_TYPE) with [TypeNumber] and []")) }
    }

    @Test
    fun `get - Tries to get but output type not supported`() {
        assertThrows<NoSuchElementException> {
            testInstance[NodeType("ANOTHER_TYPE"), listOf(), listOf(TypeNumber)]
        }.also { exception -> assertThat(exception.message, equalTo("No generator found for NodeType(ANOTHER_TYPE) with [] and [TypeNumber]")) }
    }

    private val someGeneratorOne = object : Generator {
        override val type: NodeType get() = NodeType("SOME_TYPE")
        override val inputTypes: List<TypeNew> = listOf(TypeString)
        override val outputTypes: List<TypeNew> get() = listOf()
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }
    private val someGeneratorTwo = object : Generator {
        override val type: NodeType get() = NodeType("SOME_TYPE")
        override val inputTypes: List<TypeNew> = listOf(TypeNumber)
        override val outputTypes: List<TypeNew> get() = listOf()
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val anotherGeneratorOne = object : Generator {
        override val type: NodeType get() = NodeType("ANOTHER_TYPE")
        override val inputTypes: List<TypeNew> = listOf()
        override val outputTypes: List<TypeNew> get() = listOf(TypeString)
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val anotherGeneratorTwo = object : Generator {
        override val type: NodeType get() = NodeType("ANOTHER_TYPE")
        override val inputTypes: List<TypeNew> = listOf()
        override val outputTypes: List<TypeNew> = listOf(TypeDecimal)
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val noTypeGenerator = object : Generator {
        override val type: NodeType get() = NodeType("NO_TYPE")
        override val inputTypes: List<TypeNew> = listOf()
        override val outputTypes: List<TypeNew> = listOf()
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val stringPassThroughGenerator = object : Generator {
        override val type: NodeType get() = NodeType("STRING_PASS_THROUGH")
        override val inputTypes: List<TypeNew> = listOf(TypeString)
        override val outputTypes: List<TypeNew> = listOf(TypeString)
        override fun toCode(node: Node): String {
            TODO("Not yet implemented")
        }
    }

    private val testInstance = GeneratorRegistry(
        listOf(
            someGeneratorOne,
            someGeneratorTwo,
            anotherGeneratorOne,
            anotherGeneratorTwo,
            noTypeGenerator,
            stringPassThroughGenerator
        )
    )

}