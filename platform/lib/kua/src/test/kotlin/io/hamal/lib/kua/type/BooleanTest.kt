package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.TypeSerializationFixture.generateTestCases
import org.junit.jupiter.api.TestFactory

class KuaTrueTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaTrue,
            expectedJson = """{"value":true,"type":"Boolean"}"""
        ),
        generateTestCases(
            testInstance = KuaTrue as KuaBoolean,
            expectedJson = """{"value":true,"type":"Boolean"}"""
        ),
    ).flatten()
}

class KuaFalseTypeTest {
    @TestFactory
    fun serialization() = listOf(
        generateTestCases(
            testInstance = KuaFalse,
            expectedJson = """{"value":false,"type":"Boolean"}"""
        ),
        generateTestCases(
            testInstance = KuaFalse as KuaBoolean,
            expectedJson = """{"value":false,"type":"Boolean"}"""
        ),
    ).flatten()
}