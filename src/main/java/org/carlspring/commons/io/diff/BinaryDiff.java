package org.carlspring.commons.io.diff;

import org.carlspring.commons.io.resource.ResourceCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a very simple tool that can give you an idea of where two streams
 * start having a difference. This is nothing fancy and it will stop executing
 * as soon as it has found the first difference. The main purpose of this tool
 * is to provide a way to understand where multiple digest streams start having
 * differences, thus serving as a sort of debug tool.
 *
 * @author mtodorov
 */
public class BinaryDiff
{

    private static final Logger logger = LoggerFactory.getLogger(BinaryDiff.class);

    private InputStream inputStream1;

    private InputStream inputStream2;

    private long numberOfBytesToShowUponDifference = 100;


    public BinaryDiff(InputStream inputStream1, InputStream inputStream2)
    {
        this.inputStream1 = inputStream1;
        this.inputStream2 = inputStream2;
    }

    /**
     * Checks for differences between the InputStream-s.
     *
     * @return      True, if there are differences; false otherwise.
     */
    public boolean diff()
    {
        try
        {
            byte b1[] = new byte[1];
            byte b2[] = new byte[1];
            int bytesReadFromIS1 = 0;
            int bytesReadFromIS2 = 0;
            long totalReadFromIS1 = 0L;
            long totalReadFromIS2 = 0L;

            long differencePositionStart = 0L;

            // Read byte by byte:
            while ((bytesReadFromIS1 = inputStream1.read(b1)) != -1 &&
                   (bytesReadFromIS2 = inputStream2.read(b2)) != -1)
            {
                totalReadFromIS1 += bytesReadFromIS1;
                totalReadFromIS2 += bytesReadFromIS2;

                if (b1[0] != b2[0])
                {
                    differencePositionStart = totalReadFromIS1;

                    logger.info("Byte at position " + differencePositionStart + " differs: ");

                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

                    baos1.write(b1);
                    baos1.flush();

                    baos2.write(b2);
                    baos2.flush();

                    totalReadFromIS1 = readExcerpt(inputStream1, totalReadFromIS1, b1, baos1);
                    totalReadFromIS2 = readExcerpt(inputStream2, totalReadFromIS2, b2, baos2);

                    streamsHaveDifferentLength(totalReadFromIS1, totalReadFromIS2);

                    logger.info("Displaying excerpt of data from input stream 1 after a difference was found:");
                    logger.info(baos1.toString());

                    logger.info("Displaying excerpt of data from input stream 2 after a difference was found:");
                    logger.info(baos2.toString());

                    return true;
                }
            }

            if (streamsHaveDifferentLength(totalReadFromIS1, totalReadFromIS2))
            {
                return true;
            }
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {
            ResourceCloser.close(inputStream1, logger);
            ResourceCloser.close(inputStream2, logger);
        }

        return false;
    }

    private long readExcerpt(InputStream is, long totalReadFromIS, byte[] b, ByteArrayOutputStream baos)
            throws IOException
    {
        int bytesReadFromIS;
        long bytesShown1 = 0L;
        while ((bytesReadFromIS = is.read(b)) != -1 && bytesShown1 < numberOfBytesToShowUponDifference)
        {
            totalReadFromIS += bytesReadFromIS;

            baos.write(b);
            baos.flush();

            bytesShown1++;
        }

        return totalReadFromIS;
    }

    private boolean streamsHaveDifferentLength(long totalReadFromIS1, long totalReadFromIS2)
    {
        if (totalReadFromIS1 != totalReadFromIS2)
        {
            if (totalReadFromIS1 == -1)
            {
                logger.info("Input stream 1 ended before input stream 2 at byte " + totalReadFromIS1);
            }

            if (totalReadFromIS2 == -1)
            {
                logger.info("Input stream 2 ended before input stream 1 at byte " + totalReadFromIS2);
            }

            return true;
        }

        return false;
    }

    public InputStream getInputStream1()
    {
        return inputStream1;
    }

    public void setInputStream1(InputStream inputStream1)
    {
        this.inputStream1 = inputStream1;
    }

    public InputStream getInputStream2()
    {
        return inputStream2;
    }

    public void setInputStream2(InputStream inputStream2)
    {
        this.inputStream2 = inputStream2;
    }

    public long getNumberOfBytesToShowUponDifference()
    {
        return numberOfBytesToShowUponDifference;
    }

    public void setNumberOfBytesToShowUponDifference(long numberOfBytesToShowUponDifference)
    {
        this.numberOfBytesToShowUponDifference = numberOfBytesToShowUponDifference;
    }

}
