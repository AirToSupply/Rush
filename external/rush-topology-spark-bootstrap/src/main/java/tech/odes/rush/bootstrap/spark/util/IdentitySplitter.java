package tech.odes.rush.bootstrap.spark.util;

import com.beust.jcommander.converters.IParameterSplitter;

import java.util.Collections;
import java.util.List;

/**
 * Splitter utility related to Jcommander usage of ArrayList parameters.
 */
public class IdentitySplitter implements IParameterSplitter {
    public List<String> split(String value) {
        return Collections.singletonList(value);
    }
}
