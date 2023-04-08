package internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.reflect.Modifier.isStatic;

public interface JavaReflection {

    interface Fields {

        static Predicate<Field> filterNotStatic() {
            return field -> !isStatic(field.getModifiers());
        }

        static List<Field> allFieldsOf(Class<?> fromClass) {
            return allFieldsOf(fromClass, Object.class);
        }

        static List<Field> allFieldsOf(Class<?> fromClass, Class<?> stopAtInclusive) {

            List<Field> fields = new ArrayList<>();
            List<Class<?>> classes = new ArrayList<>();

            {
                Class<?> clazz = fromClass;
                while (true) {
                    classes.add(clazz);
                    if ((clazz = clazz.getSuperclass()) == null) {
                        break;
                    }
                    if (clazz.equals(stopAtInclusive)) {
                        classes.add(clazz);
                        break;
                    }
                }
            }

            for (var clazz : classes) {
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(filterNotStatic())
                        .filter(field -> !field.getName().contains("__$")) //ignore injected fields by coverage tools
                        .forEach(fields::add);
            }
            return fields;
        }
    }

    interface Interfaces {
        static boolean implementsInterface(Class<?> targetClass, Class<?> interfaceClass) {
            return interfaceClass.isAssignableFrom(targetClass);
        }
    }
}
