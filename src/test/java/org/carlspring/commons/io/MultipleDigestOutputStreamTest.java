package org.carlspring.commons.io;

import org.carlspring.commons.encryption.EncryptionAlgorithmsEnum;
import org.carlspring.commons.util.MessageDigestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/**
 * @author mtodorov
 */
public class MultipleDigestOutputStreamTest
{
    private static final Logger logger = LoggerFactory.getLogger(MultipleDigestOutputStreamTest.class);

    @Before
    public void setUp()
            throws Exception
    {
        File dir = new File("target/test-resources");
        if (!dir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    @Test
    public void testWrite()
            throws IOException,
                   NoSuchAlgorithmException
    {
        String s = "This is a test.";

        File file = new File("target/test-resources/metadata.xml");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MultipleDigestOutputStream mdos = new MultipleDigestOutputStream(file, baos);
        
        mdos.write(s.getBytes());
        mdos.flush();

        final String md5 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.MD5.getAlgorithm());
        final String sha1 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA1.getAlgorithm());

        assertEquals("Incorrect MD5 sum!", "120ea8a25e5d487bf68b5f7096440019", md5);
        assertEquals("Incorrect SHA-1 sum!", "afa6c8b3a2fae95785dc7d9685a57835d703ac88", sha1);

        mdos.close();

        logger.debug("MD5:  " + md5);
        logger.debug("SHA1: " + sha1);

        File md5File = new File(file.getAbsolutePath() + EncryptionAlgorithmsEnum.MD5.getExtension());
        File sha1File = new File(file.getAbsolutePath() + EncryptionAlgorithmsEnum.SHA1.getExtension());

        Assert.assertTrue("Failed to create md5 checksum file!", md5File.exists());
        Assert.assertTrue("Failed to create sha1 checksum file!", sha1File.exists());

        String md5ReadIn = MessageDigestUtils.readChecksumFile(md5File.getAbsolutePath());
        String sha1ReadIn = MessageDigestUtils.readChecksumFile(sha1File.getAbsolutePath());

        assertEquals("MD5 checksum file contains incorrect checksum!", md5, md5ReadIn);
        assertEquals("SHA-1 checksum file contains incorrect checksum!", sha1, sha1ReadIn);
    }

    @Test
    public void testConcatenatedWrites()
            throws IOException,
                   NoSuchAlgorithmException
    {
        String s = "This is a big fat super long text which has no meaning, but is good for the test.";

        ByteArrayInputStream bais1 = new ByteArrayInputStream(s.getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(s.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MultipleDigestOutputStream mdos = new MultipleDigestOutputStream(baos);

        int size = 32;
        byte[] bytes = new byte[size];

        int total = 0;
        int len;

        while ((len = bais1.read(bytes, 0, size)) != -1)
        {
            mdos.write(bytes, 0, len);

            total += len;
            if (total >= size)
            {
                break;
            }
        }

        mdos.flush();

        bytes = new byte[size];
        bais1.close();

        logger.debug("Read {} bytes", total);

        bais2.skip(total);

        logger.debug("Skipped {}/{} bytes.", total, s.getBytes().length);

        while ((len = bais2.read(bytes, 0, size)) != -1)
        {
            mdos.write(bytes, 0, len);

            total += len;
        }

        mdos.flush();

        logger.debug("Original:      {}", s);
        logger.debug("Read:          {}", new String(baos.toByteArray()));

        logger.debug("Read {}/{} bytes.", total, s.getBytes().length);

        mdos.close();

        final String md5 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.MD5.getAlgorithm());
        final String sha1 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA1.getAlgorithm());

        logger.debug("MD5:  {}", md5);
        logger.debug("SHA1: {}", sha1);

        assertEquals("Incorrect MD5 sum!", "693188a2fb009bf2a87afcbca95cfcd6", md5);
        assertEquals("Incorrect SHA-1 sum!", "6ed7c74babd1609cb11836279672ade14a8748c1", sha1);
    }

}
