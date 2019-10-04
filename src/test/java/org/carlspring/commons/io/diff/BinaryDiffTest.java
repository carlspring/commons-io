package org.carlspring.commons.io.diff;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author mtodorov
 */
public class BinaryDiffTest
{
    @Test
    public void testNoDifferences()
    {
        byte[] bytes1 = "This is a test.".getBytes();
        byte[] bytes2 = "This is a test.".getBytes();

        BinaryDiff diff = new BinaryDiff(bytes1, bytes2);
        assertFalse("Reported differences where no such exit!", diff.diff());
    }

    @Test
    public void testDifferences()
    {
        byte[] bytes1 = "This is a test.".getBytes();
        byte[] bytes2 = "This is another test.".getBytes();

        BinaryDiff diff = new BinaryDiff(bytes1, bytes2);
        assertTrue("Reported no differences where such exit!", diff.diff());
    }

}
