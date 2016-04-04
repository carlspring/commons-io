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
public class RecursiveMover
        implements FileVisitor<Path>
{

    private final Path source;

    private final Path target;

    private PathTransformer pathTransformer;


    public RecursiveMover(Path source, Path target)
    {
        this.source = source;
        this.target = target;
    }

    public RecursiveMover(Path source, Path target, PathTransformer pathTransformer)
    {
        this.source = source;
        this.target = pathTransformer.getTransformedPath(source, target);
        this.pathTransformer = pathTransformer;
    }

    public FileVisitResult preVisitDirectory(Path sourcePath, BasicFileAttributes attrs)
            throws IOException
    {
        Path relativePath = relativizeTargetPath(sourcePath);
        Path targetPath = Paths.get(target.toFile().getPath() + "/" + relativePath.toFile().getPath());

        File targetFile = targetPath.toFile();
        if (sourcePath.toFile().exists() && targetFile.exists())
        {
            // 1) If its a file, delete it
            if (targetFile.isFile())
            {
                //noinspection ResultOfMethodCallIgnored
                targetFile.delete();
            }
            // 2) If its a directory, iterate and check
            else
            {
                // Carry out a directory move
                String[] paths = sourcePath.toFile().list();
                Arrays.sort(paths);

                for (String path : paths)
                {
                    Path srcPath = Paths.get(sourcePath.toFile().getPath(), path);
                    Path destPath = Paths.get(targetPath.toFile().getPath(), path);

                    File srcFile = srcPath.toFile();
                    File destFile = destPath.toFile();

                    if (srcFile.isDirectory())
                    {
                        if (!targetPath.toFile().exists())
                        {
                            // Make sure we've created the destination directory:

                            move(sourcePath, targetPath, true);
                        }
                        else
                        {
                            EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
                            RecursiveMover recursiveMover = new RecursiveMover(srcPath, destPath.getParent());
                            Files.walkFileTree(srcFile.toPath(), opts, Integer.MAX_VALUE, recursiveMover);
                        }
                    }
                    else
                    {
                        Files.move(srcFile.toPath(), destFile.toPath(), REPLACE_EXISTING);
                    }
                }
                
                if (sourcePath.toFile().list().length == 0)
                {
                    // Make sure the source directory has been removed, if its empty.
                    // This is for cases where the destination contains the directory or part of the resources.
                    //noinspection ResultOfMethodCallIgnored
                    sourcePath.toFile().delete();
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

    public FileVisitResult visitFile(Path sourcePath, BasicFileAttributes attrs)
    {
        Path relativePath = relativizeTargetPath(sourcePath);
        Path targetPath = Paths.get(target.toFile().getPath() + "/" + relativePath.toFile().getPath());

        if (!targetPath.getParent().toFile().exists())
        {
            move(sourcePath.getParent(), targetPath.getParent(), false);

            if (pathTransformer != null)
            {
                // Locate and transform the matching files here:
                // TODO: Finish up with this
                //! pathTransformer.transform();
            }

            return SKIP_SIBLINGS;
        }
        else
        {
            move(sourcePath, targetPath, false);

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

    public void move(Path source, Path target, boolean preserve)
    {
        CopyOption[] options = (preserve) ?
                               new CopyOption[]{ COPY_ATTRIBUTES, REPLACE_EXISTING } :
                               new CopyOption[]{ REPLACE_EXISTING };

        try
        {
            if (pathTransformer != null)
            {
                Files.move(source, pathTransformer.getTransformedPath(source, target), options);
            }
            else
            {
                Files.move(source, target, options);
            }
        }
        catch (FileAlreadyExistsException e)
        {
            // Ignore
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("ERROR: Unable to move " +
                               source.toFile().getAbsolutePath() + " to " +
                               target.toFile().getAbsolutePath() + "!");

            e.printStackTrace();
        }
    }

}

