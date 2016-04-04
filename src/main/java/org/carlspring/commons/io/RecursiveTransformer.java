package org.carlspring.commons.io;

import org.carlspring.commons.io.tansformers.PathTransformer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SIBLINGS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author mtodorov
 */
public class RecursiveTransformer
        implements FileVisitor<Path>
{

    private final Path source;

    private final Path target;

    private PathTransformer pathTransformer;


    public RecursiveTransformer(Path source, Path target)
    {
        this.source = source;
        this.target = target;
    }

    public RecursiveTransformer(Path source, Path target, PathTransformer pathTransformer)
    {
        this.source = source;
        this.target = pathTransformer.getTransformedPath(source, target);
        this.pathTransformer = pathTransformer;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException
    {
        return CONTINUE;
    }

    public FileVisitResult visitFile(Path sourcePath, BasicFileAttributes attrs)
    {
        Path relativePath = relativizeTargetPath(sourcePath);
        Path targetPath = Paths.get(target.toFile().getPath() + "/" + relativePath.toFile().getPath());

        if (!targetPath.getParent().toFile().exists())
        {
            if (pathTransformer != null)
            {
                // Locate and transform the matching files here:
                // TODO: Finish up with this:
                //! pathTransformer.transform();
            }

            return SKIP_SIBLINGS;
        }
        else
        {
            // TODO: Finish up with this
            //! move(sourcePath, targetPath, false);

            return CONTINUE;
        }
    }

    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
    {
        return CONTINUE;
    }

    public FileVisitResult visitFileFailed(Path file, IOException e)
    {
        if (e instanceof FileSystemLoopException)
        {
            System.err.println("Cycle detected: " + file);
        }
        else
        {
            System.err.format("Unable to move: %s: %s%n", file, e);

            e.printStackTrace();
        }

        return CONTINUE;
    }

    private Path relativizeTargetPath(Path dir)
    {
        return Paths.get(source.toFile().getName() + "/" + source.relativize(dir.toAbsolutePath()));
    }

}

