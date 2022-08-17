package tech.odes.rush.util.config;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tech.odes.rush.common.exception.RushException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFSPropertiesConfiguration {

    private static final Logger LOG = LogManager.getLogger(DFSPropertiesConfiguration.class);

    private final FileSystem fs;

    private final Path rootFile;

    private final TypedProperties props;

    // Keep track of files visited, to detect loops
    private final Set<String> visitedFiles;

    public DFSPropertiesConfiguration(FileSystem fs, Path rootFile, TypedProperties defaults) {
        this.fs = fs;
        this.rootFile = rootFile;
        this.props = defaults;
        this.visitedFiles = new HashSet<>();
        visitFile(rootFile);
    }

    public DFSPropertiesConfiguration(FileSystem fs, Path rootFile) {
        this(fs, rootFile, new TypedProperties());
    }

    public DFSPropertiesConfiguration() {
        this.fs = null;
        this.rootFile = null;
        this.props = new TypedProperties();
        this.visitedFiles = new HashSet<>();
    }

    private String[] splitProperty(String line) {
        int ind = line.indexOf('=');
        String k = line.substring(0, ind).trim();
        String v = line.substring(ind + 1).trim();
        return new String[] {k, v};
    }

    private void visitFile(Path file) {
        try {
            if (visitedFiles.contains(file.getName())) {
                throw new IllegalStateException("Loop detected; file " + file + " already referenced");
            }
            visitedFiles.add(file.getName());
            BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(file)));
            addProperties(reader);
        } catch (IOException ioe) {
            LOG.error("Error reading in properies from dfs", ioe);
            throw new IllegalArgumentException("Cannot read properties from dfs", ioe);
        }
    }

    public void addProperties(BufferedReader reader) throws IOException {
        try {
            reader.lines().forEach(line -> {
                if (line.startsWith("#") || line.equals("") || !line.contains("=")) {
                    return;
                }
                String[] split = splitProperty(line);
                if (line.startsWith("include=") || line.startsWith("include =")) {
                    visitFile(new Path(rootFile.getParent(), split[1]));
                } else {
                    props.setProperty(split[0], split[1]);
                }
            });

        } finally {
            reader.close();
        }
    }

    public TypedProperties getConfig() {
        return props;
    }

    public static DFSPropertiesConfiguration readConfig(FileSystem fs, Path cfgPath, List<String> overriddenProps) {
        DFSPropertiesConfiguration conf;
        try {
            conf = new DFSPropertiesConfiguration(cfgPath.getFileSystem(fs.getConf()), cfgPath);
        } catch (Exception e) {
            conf = new DFSPropertiesConfiguration();
            LOG.warn("Unexpected error read props file at :" + cfgPath, e);
        }

        try {
            if (!overriddenProps.isEmpty()) {
                LOG.info("Adding overridden properties to file properties.");
                conf.addProperties(new BufferedReader(new StringReader(String.join("\n", overriddenProps))));
            }
        } catch (IOException ioe) {
            throw new RushException("Unexpected error adding config overrides", ioe);
        }

        return conf;
    }

    public static DFSPropertiesConfiguration getConfig(List<String> overriddenProps) {
        DFSPropertiesConfiguration conf = new DFSPropertiesConfiguration();
        try {
            if (!overriddenProps.isEmpty()) {
                LOG.info("Adding overridden properties to file properties.");
                conf.addProperties(new BufferedReader(new StringReader(String.join("\n", overriddenProps))));
            }
        } catch (IOException ioe) {
            throw new RushException("Unexpected error adding config overrides", ioe);
        }

        return conf;
    }
}
