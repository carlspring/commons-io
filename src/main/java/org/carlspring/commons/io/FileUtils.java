package org.carlspring.commons.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author mtodorov
 */
public class FileUtils
{

    public static void moveDirectory(Path srcPath, Path destPath)
            throws IOException
    {
        if (!srcPath.toFile().isDirectory())
        {
            throw new IOException(srcPath.toAbsolutePath().toString() + " is not a directory!");
        }

        if (!destPath.toFile().exists())
        {
            //noinspection ResultOfMethodCallIgnored
            destPath.toFile().mkdirs();
        }

        Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
    }

}
