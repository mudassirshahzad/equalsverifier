package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveCollectionFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGLIST_TYPETAG = new TypeTag(List.class, STRING_TYPETAG);
    private static final TypeTag STRINGSET_TYPETAG = new TypeTag(Set.class, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDLIST_TYPETAG = new TypeTag(List.class, OBJECT_TYPETAG);
    private static final TypeTag RAWLIST_TYPETAG = new TypeTag(List.class);

    private static final ReflectiveCollectionFactory<List> LIST_FACTORY = stub("java.util.ArrayList");
    private static final ReflectiveCollectionFactory<Set> SET_FACTORY = stub("java.util.HashSet");

    private final PrefabValues prefabValues = new PrefabValues();
    private final LinkedHashSet<TypeTag> typeStack = new LinkedHashSet<>();
    private String red;
    private String black;
    private Object redObject;
    private Object blackObject;

    @Before
    public void setUp() {
        JavaApiPrefabValues.addTo(prefabValues);
        red = prefabValues.giveRed(STRING_TYPETAG);
        black = prefabValues.giveBlack(STRING_TYPETAG);
        redObject = prefabValues.giveRed(OBJECT_TYPETAG);
        blackObject = prefabValues.giveBlack(OBJECT_TYPETAG);
    }

    @Test
    public void createListsOfString() {
        Tuple<List> tuple = LIST_FACTORY.createValues(STRINGLIST_TYPETAG, prefabValues, typeStack);
        assertEquals(listOf(red), tuple.getRed());
        assertEquals(listOf(black), tuple.getBlack());
    }

    @Test
    public void createSetsOfString() {
        Tuple<Set> tuple = SET_FACTORY.createValues(STRINGSET_TYPETAG, prefabValues, typeStack);
        assertEquals(setOf(red), tuple.getRed());
        assertEquals(setOf(black), tuple.getBlack());
    }

    @Test
    public void createListsOfWildcard() {
        Tuple<List> tuple = LIST_FACTORY.createValues(WILDCARDLIST_TYPETAG, prefabValues, typeStack);
        assertEquals(listOf(redObject), tuple.getRed());
        assertEquals(listOf(blackObject), tuple.getBlack());
    }

    @Test
    public void createRawLists() {
        Tuple<List> tuple = LIST_FACTORY.createValues(RAWLIST_TYPETAG, prefabValues, typeStack);
        assertEquals(listOf(redObject), tuple.getRed());
        assertEquals(listOf(blackObject), tuple.getBlack());
    }

    private static <T> ReflectiveCollectionFactory<T> stub(String typeName) {
        return new ReflectiveCollectionFactory<>(
            typeName,
            () -> new ConditionalInstantiator(typeName).instantiate(classes(), objects()));
    }

    @SafeVarargs
    private final <T> List<T> listOf(T... values) {
        List<T> result = new ArrayList<>();
        Collections.addAll(result, values);
        return result;
    }

    @SafeVarargs
    private final <T> Set<T> setOf(T... values) {
        Set<T> result = new HashSet<>();
        Collections.addAll(result, values);
        return result;
    }
}

