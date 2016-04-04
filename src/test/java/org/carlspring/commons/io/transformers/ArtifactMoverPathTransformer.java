package org.carlspring.commons.io.transformers;

import org.carlspring.commons.io.tansformers.PathTransformer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This transformer handles Maven artifact moving.
 *
 * Example for renaming artifact com.foo:bar:1.2.3 to com.foo:blah:1.2.3
 *    source: com/foo/bar/1.2.3/bar-1.2.3.jar
 *    target: com/foo/blah/1.2.3/blah-1.2.3.jar
 *
 * An invocation of the recursive mover without using this ArtifactMoverPathTransformer
 * will instead produce the following incorrect results:
 *    source: com/foo/bar/1.2.3/bar-1.2.3.jar
 *    target: com/foo/blah/1.2.3/bar-1.2.3.jar
 *
 * @author carlspring
 */
public class ArtifactMoverPathTransformer implements PathTransformer
{


    public ArtifactMoverPathTransformer()
    {
    }

    public Path getTransformedPath(Path sourcePath, Path targetPath)
    {
        if (sourcePath.toFile().isFile() && !sourcePath.toFile().getName().equals("maven-metadata.xml") &&
            !targetPath.toFile().getName().equals("maven-metadata.xml"))
        {
            // Invoke getParent() twice in order to get the artifactId part
            Path sourceArtifactPath = sourcePath.getParent().getParent();
            Path targetArtifactPath = targetPath.getParent().getParent();

            String sourceArtifactId = sourceArtifactPath.toFile().getName();
            String targetArtifactId = targetArtifactPath.toFile().getName();

            String targetFileName = targetPath.toFile().getName();

            @SuppressWarnings("UnnecessaryLocalVariable")
            Path transformedPath = Paths.get(targetPath.getParent().toFile().getPath(),
                                             targetFileName.replace(sourceArtifactId, targetArtifactId));

            return transformedPath;
        }
        else
        {
            return targetPath;
        }
    }

    public void transform(Path sourcePath, Path targetPath)
    {
        Path transformedPath = getTransformedPath(sourcePath, targetPath);
        File transformedFile = transformedPath.toFile();

        if (!sourcePath.toFile().getName().equals("maven-metadata.xml"))
        {
            System.out.println("Renaming " + sourcePath + " to " + targetPath);

            // This has already been moved, proceed with the name transformation:
            //noinspection ResultOfMethodCallIgnored
            targetPath.toFile().renameTo(transformedFile);

            System.out.println("Renamed " + sourcePath + " to " + targetPath);
        }
    }

}
