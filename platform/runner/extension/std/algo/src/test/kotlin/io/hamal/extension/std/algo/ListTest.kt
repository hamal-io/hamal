package io.hamal.extension.std.algo

import io.hamal.runner.test.RunnerFixture.unitOfWork
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal object ListTest : AbstractTest() {

    @BeforeEach
    fun setup() {
        InvokeFunction.invocations = 0
    }

    @Test
    @Disabled
    fun `Nested functions which should be used only once`() {
        runTest(
            unitOfWork(
                """
               print('hello world')
                algo = require('std.algo').create()
                list = algo.list
                
                local l = list.create({capacity = 3})
                list.add(l, 1)
                list.add(l, 2)
                list.add(l, 3)
                
                list.length(l)
                
                list.replace(l, 1, 4)
                
                list.get(l, 12)
                list.at(l, 1)
                
                list.index_of(l, value)
                
            """
            )
        )

    }

}