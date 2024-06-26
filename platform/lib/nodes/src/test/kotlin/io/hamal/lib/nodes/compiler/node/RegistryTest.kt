//package io.hamal.lib.nodes.compiler.node
//
//import io.hamal.lib.common.value.*
//import io.hamal.lib.nodes.NodeType
//import io.hamal.lib.nodes.NodeType.Companion.NodeType
//import org.hamcrest.CoreMatchers.equalTo
//import org.hamcrest.MatcherAssert.assertThat
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//internal class AbstractNodeRegistryTest {
//
//    @Test
//    fun `get - Gets generator with no input and output types`() {
//        val result = testInstance[NodeType("NO_TYPE"), listOf(), listOf()]
//        assertThat(result, equalTo(noTypeNodeCompiler))
//    }
//
//    @Test
//    fun `get - Gets pass through generator`() {
//        val result = testInstance[NodeType("STRING_PASS_THROUGH"), listOf(TypeString), listOf(TypeString)]
//        assertThat(result, equalTo(stringPassThroughNodeCompiler))
//    }
//
//    @Test
//    fun `get - Gets generator with input types and not output types`() {
//        val result = testInstance[NodeType("SOME_TYPE"), listOf(TypeNumber), listOf()]
//        assertThat(result, equalTo(someNodeCompilerTwo))
//    }
//
//    @Test
//    fun `get - Gets generator with output types and not input types`() {
//        val result = testInstance[NodeType("ANOTHER_TYPE"), listOf(), listOf(TypeDecimal)]
//        assertThat(result, equalTo(anotherNodeCompilerTwo))
//    }
//
//    @Test
//    fun `get - Tries to get but node type not registered`() {
//        assertThrows<NoSuchElementException> {
//            testInstance[NodeType("DOES_NOT_EXISTS"), listOf(), listOf()]
//        }.also { exception ->
//            assertThat(
//                exception.message,
//                equalTo("No generator found for DOES_NOT_EXISTS with [] and []")
//            )
//        }
//    }
//
//    @Test
//    fun `get - Tries to get but input type not supported`() {
//        assertThrows<NoSuchElementException> {
//            testInstance[NodeType("ANOTHER_TYPE"), listOf(TypeNumber), listOf()]
//        }.also { exception ->
//            assertThat(
//                exception.message,
//                equalTo("No generator found for ANOTHER_TYPE with [TypeNumber] and []")
//            )
//        }
//    }
//
//    @Test
//    fun `get - Tries to get but output type not supported`() {
//        assertThrows<NoSuchElementException> {
//            testInstance[NodeType("ANOTHER_TYPE"), listOf(), listOf(TypeNumber)]
//        }.also { exception ->
//            assertThat(
//                exception.message,
//                equalTo("No generator found for ANOTHER_TYPE with [] and [TypeNumber]")
//            )
//        }
//    }
//
//    private val someNodeCompilerOne = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("SOME_TYPE")
//        override val inputs: List<ValueType> = listOf(TypeString)
//        override val outputs: List<ValueType> get() = listOf()
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//    private val someNodeCompilerTwo = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("SOME_TYPE")
//        override val inputs: List<ValueType> = listOf(TypeNumber)
//        override val outputs: List<ValueType> get() = listOf()
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val anotherNodeCompilerOne = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("ANOTHER_TYPE")
//        override val inputs: List<ValueType> = listOf()
//        override val outputs: List<ValueType> get() = listOf(TypeString)
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val anotherNodeCompilerTwo = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("ANOTHER_TYPE")
//        override val inputs: List<ValueType> = listOf()
//        override val outputs: List<ValueType> = listOf(TypeDecimal)
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val noTypeNodeCompiler = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("NO_TYPE")
//        override val inputs: List<ValueType> = listOf()
//        override val outputs: List<ValueType> = listOf()
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val stringPassThroughNodeCompiler = object : AbstractNode() {
//        override val type: NodeType get() = NodeType("STRING_PASS_THROUGH")
//        override val inputs: List<ValueType> = listOf(TypeString)
//        override val outputs: List<ValueType> = listOf(TypeString)
//        override fun toCode(ctx: Context): ValueCode {
//            TODO("Not yet implemented")
//        }
//    }
//
//    private val testInstance = NodeCompilerRegistry(
//        listOf(
//            someNodeCompilerOne,
//            someNodeCompilerTwo,
//            anotherNodeCompilerOne,
//            anotherNodeCompilerTwo,
//            noTypeNodeCompiler,
//            stringPassThroughNodeCompiler
//        )
//    )
//
//}