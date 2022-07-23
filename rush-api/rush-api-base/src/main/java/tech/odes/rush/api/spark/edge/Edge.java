package tech.odes.rush.api.spark.edge;

/**
 * a interface indicates the specific operation of data flowing from one endpoint to another
 *
 * E: runtime context about configuration and operation handle.
 *
 * C: data cell or abstraction of data model.
 */
public interface Edge<E, C> {
    /**
     * edge unique identification or data operation name
     *
     * @return
     */
    String name();

    /**
     * Indicates the specific operation of data flowing from one endpoint to another.
     *
     * @param env
     *
     * @param cell
     *
     * @return
     */
    C process(E env, C cell);
}
