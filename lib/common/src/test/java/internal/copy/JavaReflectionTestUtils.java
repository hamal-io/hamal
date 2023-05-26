package internal.copy;

import internal.JavaReflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;

import static internal.JavaReflection.Fields.allFieldsOf;
import static internal.JavaReflection.Interfaces.implementsInterface;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Nested

public class JavaReflectionTestUtils {

    @Nested
    
    class FieldsTest {

        @Nested
        
        class BugInBigDecimalTest {

            @Test
            
            void BigDecimalBug() {
                var fields = allFieldsOf(BigDecimalTestCase.class);
                assertThat(fields, hasSize(1));
                assertThat(fields.get(0).getName(), is("delegate"));
            }


            static class BigDecimalTestCase implements Serializable {
                public static final BigDecimalTestCase ZERO = new BigDecimalTestCase(new BigDecimal(0));

                @Serial
                private static final long serialVersionUID = 1L;
                private static final MathContext mathContext = MathContext.DECIMAL64;

                private final BigDecimal delegate;

                BigDecimalTestCase(BigDecimal delegate) {
                    this.delegate = delegate;
                }
            }

        }

        @Nested
        
        class FilterNotStaticTest {

            @Test
            
            void notStaticField() throws NoSuchFieldException {
                var testInstance = JavaReflection.Fields.filterNotStatic();
                var notStaticField = getField("notStaticField");

                assertTrue(testInstance.test(notStaticField));
            }

            @Test
            
            void staticField() throws NoSuchFieldException {
                var testInstance = JavaReflection.Fields.filterNotStatic();
                var staticField = getField("staticField");

                assertFalse(testInstance.test(staticField));
            }

            private Field getField(String name) throws NoSuchFieldException {
                return SomeTestClass.class.getDeclaredField(name);
            }


            class SomeTestClass {
                private static final Integer staticField = 1234;
                Integer notStaticField;
            }
        }

        @Nested
        
        class InheritedDeclaredFieldsOfTest {

            @Test
            
            public void oneLevel() {
                var fields = allFieldsOf(Level1.class);
                assertThat(fields, hasSize(1));
                assertThat(fields.get(0).getName(), is("level1"));
            }

            @Test
            
            public void twoLevel() {
                var fields = allFieldsOf(Level2.class);
                assertThat(fields, hasSize(2));
                assertThat(fields.get(0).getName(), is("level2"));
                assertThat(fields.get(1).getName(), is("level1"));
            }

            @Test
            
            public void threeLevel() {
                var fields = allFieldsOf(Level3.class);
                assertThat(fields, hasSize(3));
                assertThat(fields.get(0).getName(), is("level3"));
                assertThat(fields.get(1).getName(), is("level2"));
                assertThat(fields.get(2).getName(), is("level1"));
            }

            @Test
            
            public void getInheritedDeclaredFields_withThreeLayer_butStopAtLayer2() {
                var fields = allFieldsOf(Level3.class, Level2.class);
                assertThat(fields, hasSize(2));
                assertThat(fields.get(0).getName(), is("level3"));
                assertThat(fields.get(1).getName(), is("level2"));
            }
        }


        private static class Level1 {
            protected Integer level1;
        }

        private static class Level2 extends Level1 {
            protected Integer level2;
        }

        private static class Level3 extends Level2 {
            protected Integer level3;
        }
    }

    @Nested
    
    class InterfacesTest {
        @Nested
        
        class ImplementsInterfaceTest {

            @Test
            
            void self() {
                assertTrue(implementsInterface(TestInterface.class, TestInterface.class));
            }

            @Test
            
            void extendedInterface() {
                assertTrue(implementsInterface(ExtendsTestInterface.class, TestInterface.class));
            }

            @Test
            
            void implementingClass() {
                assertTrue(implementsInterface(ImplementsTestInterface.class, TestInterface.class));
            }

            @Test
            
            void implementingExtendedClass() {
                assertTrue(implementsInterface(ImplementsExtendsInterface.class, TestInterface.class));
            }

            @Test
            
            void inheritInterface() {
                assertTrue(implementsInterface(InheritsTestInterface.class, TestInterface.class));
            }

            @Test
            
            void notImplementingExtendedInterface() {
                assertFalse(implementsInterface(ImplementsTestInterface.class, ExtendsTestInterface.class));
            }

            private interface TestInterface {
            }

            private interface ExtendsTestInterface extends TestInterface {
            }

            private static class ImplementsTestInterface implements TestInterface {
            }

            private class ImplementsExtendsInterface implements ExtendsTestInterface {
            }

            private class InheritsTestInterface extends ImplementsTestInterface {
            }
        }
    }

}
