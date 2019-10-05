package org.carlspring.commons.io;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SIBLINGS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author mtodorov
 */
public class RecursiveMover
        implements FileVisitor<Path>
{

    private static final Logger logger = LoggerFactory.getLogger(RecursiveMover.class);

    private final Path source;

    private final Path target;


    public RecursiveMover(Path source,
                          Path target)
    {
        this.source = source;
        this.target = target;
    }

    public FileVisitResult preVisitDirectory(Path sourcePath,
                                             BasicFileAttributes attrs)
            throws IOException
    {
        Path relativePath = relativizeTargetPath(sourcePath);
        Path targetPath = target.resolve(relativePath);

        if (Files.exists(sourcePath) && Files.exists(targetPath))
        {
            // 1) If its a file, delete it
            if (Files.isRegularFile(sourcePath))
            {
                Files.deleteIfExists(sourcePath);
            }
            // 2) If its a directory, iterate and check
            else
            {
                // Carry out a directory move
                String[] paths = sourcePath.toFile().list();
                if (paths != null)
                {
                    Arrays.sort(paths);

                    for (String path : paths)
                    {
                        Path srcPath = sourcePath.resolve(path);
                        Path destPath = targetPath.resolve(path);

                        if (Files.isDirectory(srcPath))
                        {
                            if (Files.notExists(targetPath))
                            {
                                // Make sure we've created the destination directory:

                                move(sourcePath, targetPath, true);
                            }
                            else
                            {
                                EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
                                RecursiveMover recursiveMover = new RecursiveMover(srcPath, destPath.getParent());
                                Files.walkFileTree(srcPath, opts, Integer.MAX_VALUE, recursiveMover);
                            }
                        }
                        else
                        {
                            Files.move(srcPath, destPath, REPLACE_EXISTING);
                        }
                    }

                    if (paths.length == 0)
                    {
                        // Make sure the source directory has been removed, if its empty.
                        // This is for cases where the destination contains the directory or part of the resources.
                        Files.deleteIfExists(sourcePath);
                    }
                }

                return SKIP_SIBLINGS;
            }
        }

        return CONTINUE;
    }

    private Path relativizeTargetPath(Path dir)
    {
        return Paths.get(source.toFile().getName() + "/" + source.relativize(dir.toAbsolutePath()));
    }

    public FileVisitResult visitFile(Path sourcePath,
                                     BasicFileAttributes attrs)
    {
        Path relativePath = relativizeTargetPath(sourcePath);
        Path targetPath = target.resolve(relativePath);

        if (Files.notExists(targetPath.getParent()))
        {
            move(sourcePath.getParent(), targetPath.getParent(), false);

            return SKIP_SIBLINGS;
        }
        else
        {
            move(sourcePath, targetPath, false);

            return CONTINUE;
        }
    }

    public FileVisitResult postVisitDirectory(Path dir,
                                              IOException exc)
    {
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file,
                                           IOException e)
    {
        if (e instanceof FileSystemLoopException)
        {
            logger.error("Cycle detected: {}", file);
        }
        else
        {
            logger.error("Unable to move: {}", file, e);
        }

        return CONTINUE;
    }

    public void move(Path source,
                     Path target,
                     boolean preserve)
    {
        CopyOption[] options = (preserve) ?
                               new CopyOption[]{ COPY_ATTRIBUTES,
                                                 REPLACE_EXISTING } :
                               new CopyOption[]{ REPLACE_EXISTING };

        try
        {
            Files.move(source, target, options);
        }
        catch (FileAlreadyExistsException e)
        {
            // Ignore
            logger.error("File already exists", e);
        }
        catch (IOException e)
        {
            logger.error("ERROR: Unable to move {} to {}!",
                         source.toAbsolutePath(),
                         target.toAbsolutePath(), e);
        }
    }

}

