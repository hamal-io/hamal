//package io.hamal.lib.script.impl.ast.stmt
//
//import io.hamal.lib.script.impl.ast.expr.NilLiteral
//import io.hamal.lib.script.impl.ast.stmt.DoStmt.Parse
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.DynamicTest.dynamicTest
//import org.junit.jupiter.api.TestFactory
//
//internal class DoStmtTest : AbstractStatementTest() {
//    @TestFactory
//    fun parse() = listOf(
//        "do end" to DoStmt(Block.empty),
//        "do return end" to DoStmt(Block(Return(NilLiteral)))
//    ).map { (code, expected) ->
//        dynamicTest(code) {
//            runTest(Parse, code) { result, tokens ->
//                assertThat(result, equalTo(expected))
//                tokens.consumed()
//            }
//        }
//    }
//}