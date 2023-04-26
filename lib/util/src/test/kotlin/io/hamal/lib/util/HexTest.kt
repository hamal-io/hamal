package io.hamal.lib.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HexTest{
 
    @Nested
    @DisplayName("validHexNumber()")
    inner class ValidHexNumberTest{
        @Test
        fun `Valid hex string with 0x prefix`(){
            assertTrue(Hex.isValidHexNumber("0xBADc0d3"))
        }

        @Test
        fun `Valid hex string without prefix`(){
            assertTrue(Hex.isValidHexNumber("BADc0d3"))
        }

        @Test
        fun `Invalid hex string`(){
            assertFalse(Hex.isValidHexNumber("SomethingElse"))
        }

        @Test
        fun `Empty string`(){
            assertFalse(Hex.isValidHexNumber(""))
        }

        @Test
        fun `Contains white space`(){
            assertFalse(Hex.isValidHexNumber("0xBadC0d3 "))
            assertFalse(Hex.isValidHexNumber(" 0xBadC0d3"))
            assertFalse(Hex.isValidHexNumber("0xBad C0d3"))
        }

    }
}