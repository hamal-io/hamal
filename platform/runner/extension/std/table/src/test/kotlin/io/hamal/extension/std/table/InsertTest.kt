package io.hamal.extension.std.table

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.Test

internal class InsertTest : AbstractTest() {

    @Test
    fun `Insert into empty table`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { }
                
                table.insert(test_instance, 'Hamal')
                assert( table.length(test_instance) == 1 )
                
                table.insert(test_instance, 'Rocks')
                assert( table.length(test_instance) == 2 )
                assert( test_instance[2] == 'Rocks' ) 
            """
            )
        )
    }


    @Test
    fun `Insert into specific position`() {
        runTest(
            unitOfWork(
                """
                table = require('std.table').create()
                test_instance = { }
                
                table.insert(test_instance, 'Hamal')
                assert( table.length(test_instance) == 1 )
                
                table.insert(test_instance, 1, 'Rocks')
                assert( table.length(test_instance) == 2 )
                assert( test_instance[1] == 'Rocks' )
                assert( test_instance[2] == 'Hamal' )
            """
            )
        )
    }
}
