//package io.hamal.lib.util
//
//import io.hamal.lib.util.Reflection.declaredProperty
//import io.hamal.lib.util.Reflection.memberProperty
//import io.hamal.lib.util.Reflection.setPropertyValue
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.*
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//
//@Nested
//class ReflectionTest {
//
//    @Nested
//    @DisplayName("memberPropertiesOf()")
//    inner class MemberPropertiesOfTest {
//
//        @Test
//        fun `Properties of class without props`() {
//            val result = Reflection.memberPropertiesOf(Empty::class)
//            assertThat(result, hasSize(0))
//        }
//
//        @Test
//        fun `Properties of base class`() {
//            val result = Reflection.memberPropertiesOf(Root::class)
//            assertThat(result, hasSize(1))
//            assertThat(result.first().name, equalTo("root"))
//            assertThat(result.first().returnType.classifier, equalTo(Int::class))
//        }
//
//        @Test
//        fun `Properties of nested class`() {
//            val result = Reflection.memberPropertiesOf(LevelTwo::class)
//            assertThat(result, hasSize(3))
//
//            val levelTwo = result[0]
//            assertThat(levelTwo.name, equalTo("levelTwo"))
//            assertThat(levelTwo.returnType.classifier, equalTo(String::class))
//
//            val levelOne = result[1]
//            assertThat(levelOne.name, equalTo("levelOne"))
//            assertThat(levelOne.returnType.classifier, equalTo(Double::class))
//
//            val root = result[2]
//            assertThat(root.name, equalTo("root"))
//            assertThat(root.returnType.classifier, equalTo(Int::class))
//        }
//    }
//
//    @Nested
//    @DisplayName("memberPropertyOf()")
//    inner class MemberPropertyOfTest {
//        @Test
//        fun `Member property does not exists`() {
//            val levelTwo = LevelTwo("SomeValue")
//            val result = levelTwo.memberProperty("does-not-exists")
//            assertThat(result, nullValue())
//        }
//
//        @Test
//        fun `Returns member property - in same class`() {
//            val levelTwo = LevelTwo("levelTwoValue")
//            val result = levelTwo.memberProperty("levelTwo")
//            assertThat(result?.name, equalTo("levelTwo"))
//            assertThat(result?.returnType?.classifier, equalTo(String::class))
//        }
//
//        @Test
//        fun `Returns member property - in parent class`() {
//            val levelTwo = LevelTwo("levelTwoValue")
//            val result = levelTwo.memberProperty("root")
//            assertThat(result?.name, equalTo("root"))
//            assertThat(result?.returnType?.classifier, equalTo(Int::class))
//        }
//    }
//
//    @Nested
//    @DisplayName("declaredPropertiesOf()")
//    inner class DeclaredPropertiesOfTest {
//
//        @Test
//        fun `Properties of class without props`() {
//            val result = Reflection.declaredPropertiesOf(Empty::class)
//            assertThat(result, hasSize(0))
//        }
//
//        @Test
//        fun `Properties of base class`() {
//            val result = Reflection.declaredPropertiesOf(Root::class)
//            assertThat(result, hasSize(1))
//            assertThat(result.first().name, equalTo("root"))
//            assertThat(result.first().returnType.classifier, equalTo(Int::class))
//        }
//
//        @Test
//        fun `Properties of nested class`() {
//            val result = Reflection.declaredPropertiesOf(LevelTwo::class)
//            assertThat(result, hasSize(1))
//
//            val levelTwo = result[0]
//            assertThat(levelTwo.name, equalTo("levelTwo"))
//            assertThat(levelTwo.returnType.classifier, equalTo(String::class))
//        }
//    }
//
//    @Nested
//    @DisplayName("declaredPropertyOf()")
//    inner class DeclaredPropertyOfTest {
//        @Test
//        fun `Declared property does not exists`() {
//            val levelTwo = LevelTwo("SomeValue")
//            val result = levelTwo.declaredProperty("does-not-exists")
//            assertThat(result, nullValue())
//        }
//
//        @Test
//        fun `Property in parent class is not a declared property`() {
//            val levelTwo = LevelTwo("levelTwoValue")
//            val result = levelTwo.declaredProperty("root")
//            assertThat(result, nullValue())
//        }
//
//        @Test
//        fun `Returns declared property - in parent class`() {
//            val levelTwo = LevelTwo("levelTwoValue")
//            val result = levelTwo.memberProperty("levelTwo")
//            assertThat(result?.name, equalTo("levelTwo"))
//            assertThat(result?.returnType?.classifier, equalTo(String::class))
//        }
//    }
//
//    @Nested
//    @DisplayName("setPropertyValue()")
//    inner class SetPropertyValueTest {
//
//        @Test
//        fun `Replaces value of property`() {
//            val testInstance = ComposedRoot(2810, 13.37)
//            testInstance.setPropertyValue("x", 1337)
//            testInstance.setPropertyValue("y", 28.10)
//
//            assertThat(testInstance.x, equalTo(1337))
//            assertThat(testInstance.y, equalTo(28.10))
//        }
//
//        open inner class ComposedRoot(var x: Int, var y: Double)
//
//
//    }
//
//    inner class Empty {}
//
//    open inner class Root(val root: Int)
//
//    open inner class LevelOne(val levelOne: Double) : Root(0)
//
//    open inner class LevelTwo(val levelTwo: String) : LevelOne(28.10)
//}
