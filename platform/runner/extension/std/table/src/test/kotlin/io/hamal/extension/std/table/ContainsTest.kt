package io.hamal.extension.std.table

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class ContainsTest : AbstractTest() {
    @Test
    fun `Empty table`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { }
                
                assert( table.contains(test_instance, 23) == false )
            """
            )
        )

    }

    @Test
    fun `Does not contain value`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { 1234123 }
                
                assert( table.contains(test_instance, 23) == false )
            """
            )
        )
    }

    @Test
    fun `Contains value`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { 42 }
                
                assert( table.contains(test_instance, 42) == true )
            """
            )
        )
    }
}

