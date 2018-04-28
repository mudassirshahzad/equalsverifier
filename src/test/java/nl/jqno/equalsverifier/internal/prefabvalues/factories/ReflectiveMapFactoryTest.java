package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static nl.jqno.equalsverifier.internal.reflection.Util.classes;
import static nl.jqno.equalsverifier.internal.reflection.Util.objects;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("rawtypes")
public class ReflectiveMapFactoryTest {
    private static final TypeTag STRING_TYPETAG = new TypeTag(String.class);
    private static final TypeTag STRINGSTRINGMAP_TYPETAG = new TypeTag(Map.class, STRING_TYPETAG, STRING_TYPETAG);
    private static final TypeTag OBJECT_TYPETAG = new TypeTag(Object.class);
    private static final TypeTag WILDCARDMAP_TYPETAG = new TypeTag(Map.class, OBJECT_TYPETAG, OBJECT_TYPETAG);
    private static final TypeTag RAWMAP_TYPETAG = new TypeTag(Map.class);

    private static final ReflectiveMapFactory<Map> MAP_FACTORY = stub("java.util.HashMap");

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
    public void createMapsOfStringToString() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(STRINGSTRINGMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(red, black), tuple.getRed());
        assertEquals(mapOf(black, black), tuple.getBlack());
    }

    @Test
    public void createMapsOfWildcard() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(WILDCARDMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    @Test
    public void createRawMaps() {
        Tuple<Map> tuple = MAP_FACTORY.createValues(RAWMAP_TYPETAG, prefabValues, typeStack);
        assertEquals(mapOf(redObject, blackObject), tuple.getRed());
        assertEquals(mapOf(blackObject, blackObject), tuple.getBlack());
    }

    private static <T> ReflectiveMapFactory<T> stub(String typeName) {
        return new ReflectiveMapFactory<>(
            typeName,
            () -> new ConditionalInstantiator(typeName).instantiate(classes(), objects()));
    }

    private <K, V> Map<K, V> mapOf(K key, V value) {
        Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
