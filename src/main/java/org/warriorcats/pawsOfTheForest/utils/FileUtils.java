package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for file system operations and configuration management.
 * 
 * <p>This class provides comprehensive file manipulation capabilities including
 * configuration file loading/saving, directory operations, ZIP file handling, and
 * resource management. It supports both YAML and Properties file formats and
 * includes methods for copying, deleting, and archiving files and directories.</p>
 * 
 * <p>The class automatically handles resource extraction from the plugin JAR
 * and provides safe file operations with proper error handling and logging.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FileUtils {

    /**
     * The plugin's data folder where all configuration and data files are stored.
     */
    public static final File PLUGIN_DATA_FOLDER = PawsOfTheForest.getInstance().getDataFolder();

    /**
     * Default filename for the resource pack ZIP file.
     */
    public static final String RESOURCES_PACK_PATH = "resources_pack.zip";

    /**
     * Checks if a filename has a YAML extension.
     * 
     * @param fileName the filename to check
     * @return true if the filename ends with ".yaml", false otherwise
     */
    public static boolean isYaml(String fileName) {
        return fileName.endsWith(".yaml");
    }

    /**
     * Loads a configuration file (YAML or Properties) from the plugin data folder.
     * 
     * <p>If the file doesn't exist, it attempts to copy the default version from
     * the plugin JAR resources. Supports both YAML and Properties file formats
     * based on the file extension.</p>
     * 
     * @param <T> the type of configuration object (YamlConfiguration or Properties)
     * @param fileName the name of the file to load
     * @param source the configuration object to populate
     * @return the loaded configuration object
     */
    public static <T> T load(String fileName, T source) {
        File file = new File(PLUGIN_DATA_FOLDER, fileName);

        // If the file does not exist, copy default from plugin JAR
        if (!file.exists()) {
            try (InputStream in = PawsOfTheForest.class.getClassLoader().getResourceAsStream(fileName)) {
                if (in != null) {
                    file.getParentFile().mkdirs();
                    try (OutputStream out = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not copy default config: " + fileName, e);
            }
        }

        if (isYaml(fileName)) {
            source = (T) YamlConfiguration.loadConfiguration(file);
        } else {
            // Handle Properties file format
            try (InputStream input = new FileInputStream(file)) {
                ((Properties) source).load(input);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not load: " + fileName, ex);
            }
        }

        return source;
    }

    /**
     * Saves a configuration object to a file in the plugin data folder.
     * 
     * <p>Supports both YAML and Properties file formats based on the file extension.</p>
     * 
     * @param <T> the type of configuration object (YamlConfiguration or Properties)
     * @param fileName the name of the file to save
     * @param config the configuration object to save
     */
    public static <T> void store(String fileName, T config) {
        File file = new File(PLUGIN_DATA_FOLDER, fileName);

        try (OutputStream output = new FileOutputStream(file)) {
            if (isYaml(fileName)) {
                ((YamlConfiguration) config).save(file);
            } else {
                // Handle Properties file format
                ((Properties) config).store(output, "Updated by PawsOfTheForest plugin");
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not store in config file: " + fileName, e);
        }
    }

    /**
     * Recursively copies a folder and all its contents to a target location.
     * 
     * <p>Creates the target directory structure if it doesn't exist and
     * replaces existing files. Uses a file visitor pattern for efficient
     * recursive copying.</p>
     * 
     * @param source the source folder path
     * @param target the target folder path
     */
    public static void copyFolder(Path source, Path target) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(dir);
                    Path destDir = target.resolve(rel);
                    if (Files.notExists(destDir)) {
                        Files.createDirectories(destDir);
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(file);
                    Path destFile = target.resolve(rel);
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to copy directory", e);
        }
    }

    /**
     * Recursively deletes a folder and all its contents.
     * 
     * <p>Uses a reverse-order deletion approach to ensure directories are
     * deleted after their contents. Logs warnings for any files that cannot
     * be deleted but continues with the operation.</p>
     * 
     * @param path the path of the folder to delete
     */
    public static void deleteFolder(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                Bukkit.getLogger().log(Level.WARNING, "Failed to delete: " + p, e);
                            }
                        });
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to delete directory", e);
        }
    }

    /**
     * Extracts a ZIP file to a target directory.
     * 
     * <p>Creates the target directory if it doesn't exist and extracts all
     * entries from the ZIP file, preserving the directory structure.</p>
     * 
     * @param zipFile the path to the ZIP file to extract
     * @param targetDir the target directory for extraction
     */
    public static void unzipFolder(Path zipFile, Path targetDir) {
        try (ZipFile zip = new ZipFile(zipFile.toFile())) {
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path outPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    try (InputStream is = zip.getInputStream(entry)) {
                        Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while unzipping", e);
        }
    }

    /**
     * Creates a ZIP archive from a source folder.
     * 
     * <p>This method creates a temporary copy of the source folder, then zips
     * the copy to avoid path conflicts. The temporary copy is automatically
     * cleaned up after the ZIP operation completes.</p>
     * 
     * @param sourceFolderPath the path to the folder to zip
     * @param zipPath the path where the ZIP file should be created
     */
    public static void zipFolder(Path sourceFolderPath, Path zipPath) {
        Path tempCopy = PLUGIN_DATA_FOLDER.toPath().resolve("resources_pack");

        try {
            // Create temporary copy to avoid path conflicts during zipping
            // Copy the source folder to a temporary location
            Files.walk(sourceFolderPath).forEach(source -> {
                try {
                    Path destination = tempCopy.resolve(sourceFolderPath.relativize(source));
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.copy(source, destination);
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error copying resources for zipping", e);
                }
            });

            // Create ZIP archive from the temporary copy
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                Files.walk(tempCopy)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            // Ensure forward slashes for ZIP entry paths (cross-platform compatibility)
                            ZipEntry zipEntry = new ZipEntry(tempCopy.relativize(path).toString().replace("\\", "/"));
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                Bukkit.getLogger().log(Level.SEVERE, "Error zipping resources", e);
                            }
                        });
            }

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create zipped file", e);
        } finally {
            deleteFolder(tempCopy);
        }
    }

}
