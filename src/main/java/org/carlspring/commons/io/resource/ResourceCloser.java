package org.carlspring.commons.io.resource;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;

/**
 * A utility class for safely closing resources and logging eventual errors.
 * The purpose of this class is to avoid boiler-plate code.
 *
 * @author mtodorov
 */
public class ResourceCloser
{

    private ResourceCloser()
    {
    }

    public static void close(Closeable resource,
                             Logger logger)
    {
        if (resource != null)
        {
            try
            {
                resource.close();
            }
            catch (IOException e)
            {
                if (logger != null)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
