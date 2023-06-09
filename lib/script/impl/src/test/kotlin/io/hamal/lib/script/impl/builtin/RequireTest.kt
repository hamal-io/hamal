package io.hamal.lib.script.impl.builtin

import io.hamal.lib.common.value.NilValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RequireTest : AbstractBuiltinTest() {
    @Test
    fun `Requires environment`() {
        val result = eval("""require('test-env')""")
        assertThat(result, equalTo(testEnv))
    }

    @Test
    fun `Requires environment and assigns it to local variable`() {
        val result = eval("""local e = require('test-env')""")
        assertThat(result, equalTo(NilValue))
        assertThat(env["e"], equalTo(testEnv))
    }

    @Test
    fun `Requires nested environment and assigns it to local variable and returns `() {
        val result = eval("""local e = require('test-env/nested-env')""")
        assertThat(result, equalTo(NilValue))
        assertThat(env["e"], equalTo(nestedTestEnv))
    }
}