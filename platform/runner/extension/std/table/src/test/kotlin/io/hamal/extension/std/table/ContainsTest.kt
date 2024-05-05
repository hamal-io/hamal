package io.hamal.extension.std.table

import io.hamal.runner.test.AbstractRunnerTest
import org.junit.jupiter.api.Test

internal class ContainsTest : AbstractRunnerTest() {
    @Test
    fun `Empty table`() {
        createTestRunner(extensionFactories = listOf(ExtensionStdTableFactory)).also { runner ->
            runner.run(
                unitOfWork(
                    """
                table = require('std.table').create()
                test_instance = { }
                
                assert( table.contains(test_instance, 23) == false )
            """
                )
            )
        }
    }

    @Test
    fun `Does not contain value`() {
        createTestRunner(extensionFactories = listOf(ExtensionStdTableFactory)).also { runner ->
            runner.run(
                unitOfWork(
                    """
                table = require('std.table').create()
                test_instance = { 1234123 }
                
                assert( table.contains(test_instance, 23) == false )
            """
                )
            )
        }
    }

    @Test
    fun `Contains value`() {
        createTestRunner(extensionFactories = listOf(ExtensionStdTableFactory)).also { runner ->
            runner.run(
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
}
