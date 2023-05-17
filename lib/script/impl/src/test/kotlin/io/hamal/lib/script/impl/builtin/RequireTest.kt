package io.hamal.lib.script.impl.builtin

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("require()")
internal class RequireTest : AbstractBuiltinTest() {
    @Test
    fun `Requires test-env environment`() {
        val result = eval("""require('test-env')""")
        assertThat(result, equalTo(TestEnv))
    }

    @Test
    fun `Requires test-env environment and assigns it to local variable`() {
        val result = eval("""local e = require('test-env')""")
        assertThat(result, equalTo(TestEnv))
    }

//    @Test
//    fun `Requires test-env environment and assigns it to local variable and returns `() {
//        val result = eval("""local e = require('test-env'); e""")
//        assertThat(result, equalTo(TestEnv))
//    }
}