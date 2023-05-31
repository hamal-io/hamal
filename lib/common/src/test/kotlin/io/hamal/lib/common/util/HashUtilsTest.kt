package io.hamal.lib.common.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class HashUtilsTest {
    @Nested
    inner class Sha256Test {
        @Test
        fun `Creates sha256 of empty string`() {
            val result = HashUtils.sha256("")
            assertThat(result, equalTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))
        }

        @Test
        fun `Creates sha256 of H4M4L`() {
            val result = HashUtils.sha256("H4M4L")
            assertThat(result, equalTo("748c9d63c920e3a26ee8665ce7cb6f32e96886e91d8af85d83ef8f467fff1858"))
        }
    }

    @Nested
    inner class MD5Test {
        @Test
        fun `Creates md5 of empty string`() {
            val result = HashUtils.md5("")
            assertThat(result, equalTo("d41d8cd98f00b204e9800998ecf8427e"))
        }

        @Test
        fun `Creates md5 of H4M4L`() {
            val result = HashUtils.md5("H4M4L")
            assertThat(result, equalTo("a8b35f4c754f1c66bc11463a43a55e85"))
        }
    }

}