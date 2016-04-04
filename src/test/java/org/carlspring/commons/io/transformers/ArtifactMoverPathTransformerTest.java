package org.carlspring.commons.io.transformers;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * @author carlspring
 */
public class ArtifactMoverPathTransformerTest
{


    private Path sourcePath = Paths.get("target/test-resources/com/foo/bar/1.2.3/bar-1.2.3.jar");

    private Path targetPath = Paths.get("target/test-resources/com/foo/blah/1.2.3/bar-1.2.3.jar");

    private Path sourceMetadataPathAtVersionLevel = Paths.get("target/test-resources/com/foo/bar/1.2.3/maven-metadata.xml");

    private Path targetMetadataPathAtVersionLevel = Paths.get("target/test-resources/com/foo/blah/1.2.3/maven-metadata.xml");

    private Path sourceMetadataPathAtArtifactLevel = Paths.get("target/test-resources/com/foo/bar/maven-metadata.xml");

    private Path targetMetadataPathAtArtifactLevel = Paths.get("target/test-resources/com/foo/blah/maven-metadata.xml");

    private ArtifactMoverPathTransformer transformer = new ArtifactMoverPathTransformer();

    private static boolean INITIALIZED = false;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Before
    public void setUp() throws Exception
    {
        if (!INITIALIZED)
        {
            File sourceDir = new File("target/test-resources/com/foo/bar/1.2.3");
            sourceDir.mkdirs();

            File targetDir = new File("target/test-resources/com/foo/blah/1.2.3");
            targetDir.mkdirs();

            Files.createFile(sourcePath);
            Files.createFile(targetPath);

            Files.createFile(sourceMetadataPathAtVersionLevel);
            Files.createFile(targetMetadataPathAtVersionLevel);

            Files.createFile(sourceMetadataPathAtArtifactLevel);
            Files.createFile(targetMetadataPathAtArtifactLevel);

            INITIALIZED = true;
        }
    }

    @Test
    public void testTransformation() throws Exception
    {
        System.out.println(transformer.getTransformedPath(sourcePath, targetPath));

        assertEquals("Failed to transform artifact path!",
                     "target/test-resources/com/foo/blah/1.2.3/blah-1.2.3.jar",
                     transformer.getTransformedPath(sourcePath, targetPath).toFile().getPath());
    }

    @Test
    public void testTransformationWithMetadataAtVersionLevel() throws Exception
    {
        System.out.println(transformer.getTransformedPath(sourceMetadataPathAtVersionLevel,
                                                          targetMetadataPathAtVersionLevel));

        assertEquals("Failed to transform artifact path!",
                     "target/test-resources/com/foo/blah/1.2.3/maven-metadata.xml",
                     transformer.getTransformedPath(sourceMetadataPathAtVersionLevel,
                                                    targetMetadataPathAtVersionLevel).toFile().getPath());
    }

    @Test
    public void testTransformationWithMetadataAtArtifactLevel() throws Exception
    {
        System.out.println(transformer.getTransformedPath(sourceMetadataPathAtArtifactLevel,
                                                          targetMetadataPathAtArtifactLevel));

        assertEquals("Failed to transform artifact path!",
                     "target/test-resources/com/foo/blah/maven-metadata.xml",
                     transformer.getTransformedPath(sourceMetadataPathAtArtifactLevel,
                                                    targetMetadataPathAtArtifactLevel).toFile().getPath());
    }

}
