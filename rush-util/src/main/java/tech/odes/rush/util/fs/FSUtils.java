package tech.odes.rush.util.fs;

import tech.odes.rush.common.exception.RushException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility functions related to accessing the file storage.
 */
public class FSUtils {

    private static final Logger LOG = LogManager.getLogger(FSUtils.class);
    // Log files are of this pattern - .b5068208-e1a4-11e6-bf01-fe55135034f3_20170101134598.log.1
    private static final Pattern LOG_FILE_PATTERN =
            Pattern.compile("\\.(.*)_(.*)\\.(.*)\\.([0-9]*)(_(([0-9]*)-([0-9]*)-([0-9]*)))?");
    private static final String LOG_FILE_PREFIX = ".";
    private static final int MAX_ATTEMPTS_RECOVER_LEASE = 10;
    private static final String RUSH_ENV_PROPS_PREFIX = "RUSH_ENV_";

    private static final PathFilter ALLOW_ALL_FILTER = file -> true;

    public static Configuration prepareHadoopConf(Configuration conf) {
        // look for all properties, prefixed to be picked up
        for (Entry<String, String> prop : System.getenv().entrySet()) {
            if (prop.getKey().startsWith(RUSH_ENV_PROPS_PREFIX)) {
                LOG.info("Picking up value for hoodie env var :" + prop.getKey());
                conf.set(prop.getKey().replace(RUSH_ENV_PROPS_PREFIX, "").replaceAll("_DOT_", "."), prop.getValue());
            }
        }
        return conf;
    }

    public static FileSystem getFs(String path, Configuration conf) {
        FileSystem fs;
        prepareHadoopConf(conf);
        try {
            fs = new Path(path).getFileSystem(conf);
        } catch (IOException e) {
            throw new RushException("Failed to get instance of " + FileSystem.class.getName(), e);
        }
        return fs;
    }

    public static long getFileSize(FileSystem fs, Path path) throws IOException {
        return fs.getFileStatus(path).getLen();
    }

    public static String getRelativePartitionPath(Path basePath, Path fullPartitionPath) {
        basePath = Path.getPathWithoutSchemeAndAuthority(basePath);
        fullPartitionPath = Path.getPathWithoutSchemeAndAuthority(fullPartitionPath);
        String fullPartitionPathStr = fullPartitionPath.toString();
        int partitionStartIndex = fullPartitionPathStr.indexOf(basePath.getName(),
                basePath.getParent() == null ? 0 : basePath.getParent().toString().length());
        // Partition-Path could be empty for non-partitioned tables
        return partitionStartIndex + basePath.getName().length() == fullPartitionPathStr.length() ? ""
                : fullPartitionPathStr.substring(partitionStartIndex + basePath.getName().length() + 1);
    }

    public static String getFileExtension(String fullName) {
        Objects.requireNonNull(fullName);
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex);
    }

    /**
     * Returns a new unique prefix for creating a file group.
     */
    public static String createNewFileIdPfx() {
        return UUID.randomUUID().toString();
    }

    public static int getDefaultBufferSize(final FileSystem fs) {
        return fs.getConf().getInt("io.file.buffer.size", 4096);
    }

    public static Short getDefaultReplication(FileSystem fs, Path path) {
        return fs.getDefaultReplication(path);
    }

    public static boolean recoverDFSFileLease(final DistributedFileSystem dfs, final Path p)
            throws IOException, InterruptedException {
        LOG.info("Recover lease on dfs file " + p);
        // initiate the recovery
        boolean recovered = false;
        for (int nbAttempt = 0; nbAttempt < MAX_ATTEMPTS_RECOVER_LEASE; nbAttempt++) {
            LOG.info("Attempt " + nbAttempt + " to recover lease on dfs file " + p);
            recovered = dfs.recoverLease(p);
            if (recovered) {
                break;
            }
            // Sleep for 1 second before trying again. Typically it takes about 2-3 seconds to recover
            // under default settings
            Thread.sleep(1000);
        }
        return recovered;
    }

    public static void createPathIfNotExists(FileSystem fs, Path partitionPath) throws IOException {
        if (!fs.exists(partitionPath)) {
            fs.mkdirs(partitionPath);
        }
    }

    public static Long getSizeInMB(long sizeInBytes) {
        return sizeInBytes / (1024 * 1024);
    }

    public static Path getPartitionPath(String basePath, String partitionPath) {
        return getPartitionPath(new Path(basePath), partitionPath);
    }

    public static Path getPartitionPath(Path basePath, String partitionPath) {
        // FOr non-partitioned table, return only base-path
        return ((partitionPath == null) || (partitionPath.isEmpty())) ? basePath : new Path(basePath, partitionPath);
    }

    public static String getDFSFullPartitionPath(FileSystem fs, Path fullPartitionPath) {
        return fs.getUri() + fullPartitionPath.toUri().getRawPath();
    }
}
