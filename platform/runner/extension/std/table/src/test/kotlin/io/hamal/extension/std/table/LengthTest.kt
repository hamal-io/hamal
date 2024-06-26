package io.hamal.extension.std.table

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class LengthTest : AbstractTest() {
    @Test
    fun `Empty table`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { }
                
                assert( table.length(test_instance) == 0 )
            """
            )
        )
    }

    @Test
    fun `Single value`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                
                test_instance = { 0  }
                assert( table.length(test_instance) == 1 )
                
                test_instance = { answer = 42 } 
                assert( table.length(test_instance) == 1 )
            """
            )
        )
    }

    @Test
    fun `Multiple values`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                
                test_instance = { 0, 2, 4, 6  }
                assert( table.length(test_instance) == 4 )
                
                test_instance = { answer = 42, nested = { counts_as = 1} , options = { } } 
                assert( table.length(test_instance) == 3 )
            """
            )
        )
    }
}
