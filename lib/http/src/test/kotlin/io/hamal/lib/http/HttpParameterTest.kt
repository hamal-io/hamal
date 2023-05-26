package io.hamal.lib.http

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HttpParameterTest {

    @Nested
    
    inner class ToQueryStringTest() {

        @Test
        fun `No parameter to build query string`() {
            val testInstance = listOf<HttpParameter>()

            val result = testInstance.toQueryString()
            assertThat(result, equalTo(""))
        }

        @Test
        fun `Creates a query string of single string parameter`() {
            val testInstance = listOf(HttpParameter("key", "value"))

            val result = testInstance.toQueryString()
            assertThat(result, equalTo("?key=value"))
        }

        @Test
        fun `Creates a query string with multiple string parameters`() {
            val testInstance = listOf(
                HttpParameter("key-one", "value-one"),
                HttpParameter("key-two", "value-two")
            )

            val result = testInstance.toQueryString()
            assertThat(result, equalTo("?key-one=value-one&key-two=value-two"))
        }

        @Test
        fun `Creates a query string with multiple numbers`() {
            val testInstance = listOf(
                HttpParameter("key-one", 42),
                HttpParameter("key-two", 13.37)
            )

            val result = testInstance.toQueryString()
            assertThat(result, equalTo("?key-one=42&key-two=13.37"))
        }

        @Test
        fun `Creates a query string with multiple booleans`() {
            val testInstance = listOf(
                HttpParameter("key-one", true),
                HttpParameter("key-two", false)
            )

            val result = testInstance.toQueryString()
            assertThat(result, equalTo("?key-one=true&key-two=false"))
        }
    }

}