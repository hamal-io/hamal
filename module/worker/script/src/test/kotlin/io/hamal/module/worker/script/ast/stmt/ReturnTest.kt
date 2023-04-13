package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.junit.jupiter.api.Test

internal class ReturnTest : AbstractAstTest() {

    @Test
    fun `Return statement without expression`() {
        val result = testInstance("return end")
        println(result)
    }

    @Test
    fun `Return statement with number literal`() {
        val result = testInstance("return 1 end")
        println(result)
    }


    private fun testInstance(code: String) = ReturnStatement.ParseReturnStatement(parserContextOf(code))
}