package org.carlspring.commons.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mtodorov
 */
class FileUtilsTest
{

    private static final long SMALL_FILE_SIZE = 8L;

    private static final Path srcDir = Paths.get("target/test-resources/src/foo").toAbsolutePath();

    private static final Logger logger = LoggerFactory.getLogger(FileUtilsTest.class);

    @BeforeEach
    void setUp()
            throws Exception
    {
        Files.createDirectories(srcDir);

        mkdirs(srcDir, "blah/blahblah", "yadee/boo/hoo");

        generateTestResource(srcDir.resolve("bar.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("blah/blah1.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("blah/blah2.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("blah/blahblah/moreblah1.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("blah/blahblah/moreblah2.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/yadda1.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/yadda2.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/yadda3.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/boo/hoo1.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/boo/hoo2.bin"), SMALL_FILE_SIZE);
        generateTestResource(srcDir.resolve("yadee/boo/hoo/wow1.bin"), SMALL_FILE_SIZE);
    }

    private static void mkdirs(Path basePath,
                               String... dirs)
            throws IOException
    {
        for (String dir : dirs)
        {
            Path directory = basePath.resolve(dir);
            Files.createDirectories(directory);
        }
    }

    private static void generateTestResource(Path filePath,
                                             long length)
            throws IOException
    {
        RandomInputStream ris = new RandomInputStream(length);
        OutputStream fos = Files.newOutputStream(filePath);

        IOUtils.copy(ris, fos);

        ris.close();
        fos.close();
    }

    @Test
    void testMoveDirectory()
            throws IOException
    {
        Path destDir = Paths.get("target/test-resources/move-directory-dest-non-existent/dest").toAbsolutePath();

        // Prepare the resources:
        Files.createDirectories(destDir);

        long startTime = System.currentTimeMillis();

        // Execute the actual test:
        FileUtils.moveDirectory(srcDir, destDir);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Path srcFile = srcDir.resolve("bar.bin");
        Path destFile = destDir.resolve("bar.bin");

        assertTrue(Files.exists(destFile), "Failed to move file!");
        assertTrue(Files.notExists(srcFile), "Failed to move file!");

        logger.debug("Successfully performed recursive directory move in {} ms, " +
                     "(where the destination directory does not contain any files in advance).", duration);
    }

    @Test
    void testMoveDirectoryWhereDestinationExists()
            throws IOException
    {
        Path destDir = Paths.get("target/test-resources/move-directory-dest-contains-foo/foo").toAbsolutePath();

        // Prepare the resources:
        Files.createDirectories(destDir);

        Path barBin = destDir.resolve("bar.bin");
        generateTestResource(barBin, 2 * SMALL_FILE_SIZE);

        Path fooDir = destDir.resolve("blah");
        Files.createDirectories(fooDir);

        long startTime = System.currentTimeMillis();

        // Follow links when copying files
        EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        RecursiveMover mover = new RecursiveMover(srcDir, destDir.getParent());
        Files.walkFileTree(srcDir, options, Integer.MAX_VALUE, mover);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Path srcFile = srcDir.resolve("bar.bin");
        Path destFile = destDir.resolve("bar.bin");

        assertTrue(Files.exists(destFile), "Failed to move file!");
        assertTrue(Files.notExists(srcFile), "Failed to move file!");

        assertEquals(SMALL_FILE_SIZE, Files.size(barBin), "Failed to replace file!");

        logger.debug("Successfully performed recursive directory move in {} ms," +
                     " (where the destination directory contains some files in advance).", duration);
    }

}
