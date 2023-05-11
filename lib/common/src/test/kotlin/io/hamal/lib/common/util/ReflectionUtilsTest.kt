package io.hamal.lib.common.util

import io.hamal.lib.common.util.ReflectionUtils.declaredProperty
import io.hamal.lib.common.util.ReflectionUtils.memberProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.Field

@Nested
class ReflectionUtilsTest {

    @Nested
    @DisplayName("memberPropertiesOf()")
    inner class MemberPropertiesOfTest {

        @Test
        fun `Properties of class without props`() {
            val result = ReflectionUtils.memberPropertiesOf(Empty::class)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Properties of base class`() {
            val result = ReflectionUtils.memberPropertiesOf(Root::class)
            assertThat(result, hasSize(1))
            assertThat(result.first().name, equalTo("root"))
            assertThat(result.first().returnType.classifier, equalTo(Int::class))
        }

        @Test
        fun `Properties of nested class`() {
            val result = ReflectionUtils.memberPropertiesOf(LevelTwo::class)
            assertThat(result, hasSize(3))

            val levelTwo = result[0]
            assertThat(levelTwo.name, equalTo("levelTwo"))
            assertThat(levelTwo.returnType.classifier, equalTo(String::class))

            val levelOne = result[1]
            assertThat(levelOne.name, equalTo("levelOne"))
            assertThat(levelOne.returnType.classifier, equalTo(Double::class))

            val root = result[2]
            assertThat(root.name, equalTo("root"))
            assertThat(root.returnType.classifier, equalTo(Int::class))
        }
    }

    @Nested
    @DisplayName("memberPropertyOf()")
    inner class MemberPropertyOfTest {
        @Test
        fun `Member property does not exists`() {
            val levelTwo = LevelTwo("SomeValue")
            val result = memberProperty(levelTwo, "does-not-exists")
            assertThat(result, nullValue())
        }

        @Test
        fun `Returns member property - in same class`() {
            val levelTwo = LevelTwo("levelTwoValue")
            val result = memberProperty(levelTwo, "levelTwo")
            assertThat(result?.name, equalTo("levelTwo"))
            assertThat(result?.returnType?.classifier, equalTo(String::class))
        }

        @Test
        fun `Returns member property - in parent class`() {
            val levelTwo = LevelTwo("levelTwoValue")
            val result = memberProperty(levelTwo, "root")
            assertThat(result?.name, equalTo("root"))
            assertThat(result?.returnType?.classifier, equalTo(Int::class))
        }
    }

    @Nested
    @DisplayName("declaredPropertiesOf()")
    inner class DeclaredPropertiesOfTest {

        @Test
        fun `Properties of class without props`() {
            val result = ReflectionUtils.declaredPropertiesOf(Empty::class)
            assertThat(result, hasSize(0))
        }

        @Test
        fun `Properties of base class`() {
            val result = ReflectionUtils.declaredPropertiesOf(Root::class)
            assertThat(result, hasSize(1))
            assertThat(result.first().name, equalTo("root"))
            assertThat(result.first().returnType.classifier, equalTo(Int::class))
        }

        @Test
        fun `Properties of nested class`() {
            val result = ReflectionUtils.declaredPropertiesOf(LevelTwo::class)
            assertThat(result, hasSize(1))

            val levelTwo = result[0]
            assertThat(levelTwo.name, equalTo("levelTwo"))
            assertThat(levelTwo.returnType.classifier, equalTo(String::class))
        }
    }

    @Nested
    @DisplayName("declaredPropertyOf()")
    inner class DeclaredPropertyOfTest {
        @Test
        fun `Declared property does not exists`() {
            val levelTwo = LevelTwo("SomeValue")
            val result = declaredProperty(levelTwo, "does-not-exists")
            assertThat(result, nullValue())
        }

        @Test
        fun `Property in parent class is not a declared property`() {
            val levelTwo = LevelTwo("levelTwoValue")
            val result = declaredProperty(levelTwo, "root")
            assertThat(result, nullValue())
        }

        @Test
        fun `Returns declared property - in parent class`() {
            val levelTwo = LevelTwo("levelTwoValue")
            val result = memberProperty(levelTwo, "levelTwo")
            assertThat(result?.name, equalTo("levelTwo"))
            assertThat(result?.returnType?.classifier, equalTo(String::class))
        }
    }


    @Nested
    @DisplayName("getValue()")
    inner class GetValueTest {

        @Test
        fun `Able to get the value from all accessible fields`() {
            val target = DerivedDerivedClass(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12
            )

            assertThat(ReflectionUtils.getValue(target, "privateBaseValue"), equalTo(1))
            assertThat(ReflectionUtils.getValue(target, "protectedBaseValue"), equalTo(2))
            assertThat(ReflectionUtils.getValue(target, "internalBaseValue"), equalTo(3))
            assertThat(ReflectionUtils.getValue(target, "publicBaseValue"), equalTo(4))
            assertThat(ReflectionUtils.getValue(target, "privateDerivedValue"), equalTo(5))
            assertThat(ReflectionUtils.getValue(target, "protectedDerivedValue"), equalTo(6))
            assertThat(ReflectionUtils.getValue(target, "internalDerivedValue"), equalTo(7))
            assertThat(ReflectionUtils.getValue(target, "publicDerivedValue"), equalTo(8))
            assertThat(ReflectionUtils.getValue(target, "privateDerivedDerivedValue"), equalTo(9))
            assertThat(ReflectionUtils.getValue(target, "protectedDerivedDerivedValue"), equalTo(10))
            assertThat(ReflectionUtils.getValue(target, "internalDerivedDerivedValue"), equalTo(11))
            assertThat(ReflectionUtils.getValue(target, "publicDerivedDerivedValue"), equalTo(12))
        }

        @Test
        fun `Able to get value from root path`() {
            val target = ComposedRoot(2810)
            assertThat(ReflectionUtils.getValue(target, "rootValue"), equalTo(2810))
        }

        @Test
        fun `Able to get values from simple nesting`() {
            val target = ComposedLevelOne(1506, ComposedRoot(2810))
            assertThat(ReflectionUtils.getValue(target, "levelOneValue"), equalTo(1506))
            assertThat(ReflectionUtils.getValue(target, "parent.rootValue"), equalTo(2810))
        }

        @Test
        fun `Able to get values from multiple nesting`() {
            val target = ComposedLevelTwo(1212, ComposedLevelOne(1506, ComposedRoot(2810)))
            assertThat(ReflectionUtils.getValue(target, "levelTwoValue"), equalTo(1212))
            assertThat(ReflectionUtils.getValue(target, "parent.levelOneValue"), equalTo(1506))
            assertThat(ReflectionUtils.getValue(target, "parent.parent.rootValue"), equalTo(2810))
        }

        @Test
        fun `Field does not exist`() {
            val target = ComposedLevelOne(1506, ComposedRoot(2810))
            var exception = assertThrows<IllegalArgumentException> {
                ReflectionUtils.getValue(target, "doesNotExists")
            }
            assertThat(exception.message, containsString("ComposedLevelOne does have field 'doesNotExists'"))

            exception = assertThrows<IllegalArgumentException> {
                ReflectionUtils.getValue(target, "parent.doesNotExists")
            }
            assertThat(exception.message, containsString("ComposedLevelOne does have field 'parent.doesNotExists'"))

            exception = assertThrows<IllegalArgumentException> {
                ReflectionUtils.getValue(target, "doesNotExists.doesNotExists")
            }
            assertThat(
                exception.message,
                containsString("ComposedLevelOne does have field 'doesNotExists.doesNotExists'")
            )
        }
    }

    @Nested
    @DisplayName("allFields()")
    inner class AllFieldsTest {
        @Test
        fun `List all fields of object`() {
            val target = DerivedDerivedClass(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12
            )
            assertResult(ReflectionUtils.allFields(target))
        }

        @Test
        fun `List all fields of KClass`() {
            val target = DerivedDerivedClass(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12
            )
            assertResult(ReflectionUtils.allFields(target::class))
        }

        @Test
        fun `List all fields of Class`() {
            val target = DerivedDerivedClass(
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12
            )
            assertResult(ReflectionUtils.allFields(target::class.java))
        }

        private fun assertResult(fields: List<Field>) {
            assertThat(fields, hasSize(12))

            val names = fields.map { it.name }.toSet()
            assertThat(names, hasItem("privateBaseValue"))
            assertThat(names, hasItem("protectedBaseValue"))
            assertThat(names, hasItem("internalBaseValue"))
            assertThat(names, hasItem("publicBaseValue"))
            assertThat(names, hasItem("privateDerivedValue"))
            assertThat(names, hasItem("protectedDerivedValue"))
            assertThat(names, hasItem("internalDerivedValue"))
            assertThat(names, hasItem("publicDerivedValue"))
            assertThat(names, hasItem("privateDerivedDerivedValue"))
            assertThat(names, hasItem("protectedDerivedDerivedValue"))
            assertThat(names, hasItem("internalDerivedDerivedValue"))
            assertThat(names, hasItem("publicDerivedDerivedValue"))
        }
    }

    inner class Empty {}

    open inner class Root(val root: Int)

    open inner class LevelOne(val levelOne: Double) : Root(0)

    open inner class LevelTwo(val levelTwo: String) : LevelOne(28.10)

    open inner class BaseClass(
        private val privateBaseValue: Int,
        protected val protectedBaseValue: Int,
        internal val internalBaseValue: Int,
        public val publicBaseValue: Int
    )

    open inner class DerivedClass(
        privateBaseValue: Int,
        protectedBaseValue: Int,
        internalBaseValue: Int,
        publicBaseValue: Int,
        private val privateDerivedValue: Int,
        private val protectedDerivedValue: Int,
        private val internalDerivedValue: Int,
        private val publicDerivedValue: Int
    ) : BaseClass(privateBaseValue, protectedBaseValue, internalBaseValue, publicBaseValue)

    inner class DerivedDerivedClass(
        privateBaseValue: Int,
        protectedBaseValue: Int,
        internalBaseValue: Int,
        publicBaseValue: Int,
        privateDerivedValue: Int,
        protectedDerivedValue: Int,
        internalDerivedValue: Int,
        publicDerivedValue: Int,
        private val privateDerivedDerivedValue: Int,
        private val protectedDerivedDerivedValue: Int,
        private val internalDerivedDerivedValue: Int,
        private val publicDerivedDerivedValue: Int
    ) : DerivedClass(
        privateBaseValue, protectedBaseValue, internalBaseValue, publicBaseValue,
        privateDerivedValue, protectedDerivedValue, internalDerivedValue, publicDerivedValue,
    )

    open inner class ComposedRoot(val rootValue: Int)

    open inner class ComposedLevelOne(val levelOneValue: Int, val parent: ComposedRoot)

    open inner class ComposedLevelTwo(val levelTwoValue: Int, val parent: ComposedLevelOne)
}
