package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.Test

internal class DoTest : AbstractAstTest() {

    @Test
    fun `do end`() {
        val result = testInstance("do end")
        println(result)
    }

    @Test
    fun `do return end`() {
        val result = testInstance("do return end")
        println(result)
    }

    private fun testInstance(code: String) = DoStatement.ParseDoBlock(parserContextOf(code))

}