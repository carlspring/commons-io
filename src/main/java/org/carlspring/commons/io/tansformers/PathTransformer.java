package org.carlspring.commons.io.tansformers;

import java.nio.file.Path;

/**
 * @author carlspring
 */
public interface PathTransformer
{

    Path getTransformedPath(Path sourcePath, Path targetPath);

    void transform(Path sourcePath, Path targetPath);

}
