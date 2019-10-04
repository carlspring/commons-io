package org.carlspring.commons.io;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author mtodorov
 */
class RandomInputStreamTest
{

    @Test
    void testIO()
            throws IOException
    {
        RandomInputStream ris = new RandomInputStream(10000);

        int total = 0;
        int len;
        int size = 4096;
        byte[] bytes = new byte[size];

        while ((len = ris.read(bytes, 0, size)) != -1)
        {
            total += len;
        }

        assertEquals(10000, total, "The number of read bytes do not match the defined size!");
    }

}
