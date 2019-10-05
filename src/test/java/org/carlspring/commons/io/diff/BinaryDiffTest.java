package org.carlspring.commons.io.diff;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mtodorov
 */
class BinaryDiffTest
{
    @Test
    void testNoDifferences()
    {
        byte[] bytes1 = "This is a test.".getBytes();
        byte[] bytes2 = "This is a test.".getBytes();

        BinaryDiff diff = new BinaryDiff(bytes1, bytes2);
        assertFalse(diff.diff(), "Reported differences where no such exit!");
    }

    @Test
    void testDifferences()
    {
        byte[] bytes1 = "This is a test.".getBytes();
        byte[] bytes2 = "This is another test.".getBytes();

        BinaryDiff diff = new BinaryDiff(bytes1, bytes2);
        assertTrue(diff.diff(), "Reported no differences where such exit!");
    }

}
