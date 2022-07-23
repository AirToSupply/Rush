package tech.odes.rush.api.spark.vertex;

/**
 * a interface indicates a data source endpoint which will import or export from the endpoint.
 *
 * E: runtime context about configuration and operation handle.
 *
 * C: data cell or abstraction of data model.
 */
public interface Vertex<E, C> {

    /**
     * import data source, it means to write data to the data source.
     *
     * @param env
     *
     * @param cell
     */
    void in(E env, C cell);

    /**
     * export data source, it means to read data from the data source.
     *
     * @param env
     *
     * @param cell
     *
     * @return
     */
    E out(E env, C cell);
}
