package io.hamal.extension.std.table

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class InsertTest : AbstractRunnerTest() {

    @Test
    fun `Insert into empty table`() {
        createTestRunner(extensionFactories = listOf(ExtensionStdTableFactory)).also { runner ->
            runner.run(
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
    }

    @Test
    fun `Insert into specific position`() {
        createTestRunner(extensionFactories = listOf(ExtensionStdTableFactory)).also { runner ->
            runner.run(
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
}
