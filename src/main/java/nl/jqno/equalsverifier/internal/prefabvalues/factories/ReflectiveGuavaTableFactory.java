package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.Comparator;
import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.reflection.Util.*;

/**
 * Implementation of {@link PrefabValueFactory} that creates instances of
 * Guava's Table using reflection (since Guava may not be on the classpath)
 * while taking generics into account.
 */
public abstract class ReflectiveGuavaTableFactory<T> implements AbstractReflectiveGenericFactory<T> {
    private final String typeName;

    /* default */ ReflectiveGuavaTableFactory(String typeName) {
        this.typeName = typeName;
    }

    public static <T> ReflectiveGuavaTableFactory<T> callFactoryMethod(final String typeName, final String methodName) {
        return new ReflectiveGuavaTableFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(), objects());
            }
        };
    }

    public static <T> ReflectiveGuavaTableFactory<T> callFactoryMethodWithComparator(
            final String typeName, final String methodName, final Object parameterValue) {
        return new ReflectiveGuavaTableFactory<T>(typeName) {
            @Override
            protected Object createEmpty() {
                return new ConditionalInstantiator(typeName)
                        .callFactory(methodName, classes(Comparator.class, Comparator.class), objects(parameterValue, parameterValue));
            }
        };
    }

    protected abstract Object createEmpty();

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        LinkedHashSet<TypeTag> clone = cloneWith(typeStack, tag);
        TypeTag columnTag = determineAndCacheActualTypeTag(0, tag, prefabValues, clone);
        TypeTag rowTag = determineAndCacheActualTypeTag(1, tag, prefabValues, clone);
        TypeTag valueTag = determineAndCacheActualTypeTag(2, tag, prefabValues, clone);

        Object red = createWith(prefabValues.giveRed(columnTag), prefabValues.giveRed(rowTag), prefabValues.giveBlack(valueTag));
        Object black = createWith(prefabValues.giveBlack(columnTag), prefabValues.giveBlack(rowTag), prefabValues.giveBlack(valueTag));
        Object redCopy = createWith(prefabValues.giveRed(columnTag), prefabValues.giveRed(rowTag), prefabValues.giveBlack(valueTag));

        return Tuple.of(red, black, redCopy);
    }

    private Object createWith(Object column, Object row, Object value) {
        Class<?> type = classForName(typeName);
        Object result = createEmpty();
        invoke(type, result, "put", classes(Object.class, Object.class, Object.class), objects(column, row, value));
        return result;
    }
}
