package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.ListBuilders;

import java.util.List;

/**
 * {@code EqualsVerifier} can be used in unit tests to verify whether the
 * contract for the {@code equals} and {@code hashCode} methods in a class is
 * met.
 * <p>
 * The contracts are described in the Javadoc comments for
 * {@link java.lang.Object#equals(Object)} and
 * {@link java.lang.Object#hashCode()}
 * <p>
 * To get started, use {@code EqualsVerifier} as follows:
 * <p>
 * {@code EqualsVerifier.forClass(My.class).verify();}
 * <p>
 * For more information, see the documentation at
 * http://www.jqno.nl/equalsverifier
 */
public final class EqualsVerifier {

    /**
     * Private constructor. Call {@link #forClass(Class)} or
     * {@link #forRelaxedEqualExamples(Object, Object, Object...)} instead.
     */
    private EqualsVerifier() {}

    /**
     * Factory method. For general use.
     *
     * @param type The class for which the {@code equals} method should be
     *          tested.
     */
    public static <T> EqualsVerifierApi<T> forClass(Class<T> type) {
        Configuration<T> config = Configuration.of(type);
        return new EqualsVerifierApi<>(config);
    }

    /**
     * Factory method. Asks for a list of equal, but not identical, instances
     * of T.
     *
     * For use when T is a class which has relaxed equality
     * rules. This happens when two instances of T are equal even though the
     * its internal state is different.
     *
     * This could happen, for example, in a Rational class that doesn't
     * normalize: new Rational(1, 2).equals(new Rational(2, 4)) would return
     * true.
     *
     * Using this factory method requires that
     * {@link RelaxedEqualsVerifierApi#andUnequalExamples(Object, Object...)}
     * be called to supply a list of unequal instances of T.
     *
     * This method automatically suppresses
     * {@link Warning#ALL_FIELDS_SHOULD_BE_USED}.
     *
     * @param first An instance of T.
     * @param second Another instance of T, which is equal, but not identical,
     *          to {@code first}.
     * @param more More instances of T, all of which are equal, but not
     *          identical, to one another and to {@code first} and
     *          {@code second}.
     */
    @SafeVarargs
    public static <T> RelaxedEqualsVerifierApi<T> forRelaxedEqualExamples(T first, T second, T... more) {
        List<T> examples = ListBuilders.buildListOfAtLeastTwo(first, second, more);

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)first.getClass();

        return new RelaxedEqualsVerifierApi<>(type, examples);
    }
}
