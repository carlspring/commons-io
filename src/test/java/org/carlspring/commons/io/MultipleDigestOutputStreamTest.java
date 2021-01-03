package org.carlspring.commons.io;

import org.carlspring.commons.encryption.EncryptionAlgorithmsEnum;
import org.carlspring.commons.util.MessageDigestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author mtodorov
 */
class MultipleDigestOutputStreamTest
{

    private static final Logger logger = LoggerFactory.getLogger(MultipleDigestOutputStreamTest.class);

    @BeforeEach
    void setUp()
            throws Exception
    {
        Path dir = Paths.get("target/test-resources").toAbsolutePath();
        if (Files.notExists(dir))
        {
            Files.createDirectories(dir);
        }
    }

    @Test
    void testWrite()
            throws IOException,
                   NoSuchAlgorithmException
    {
        String s = "This is a test.";

        Path filePath = Paths.get("target/test-resources/metadata.xml").toAbsolutePath();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MultipleDigestOutputStream mdos = new MultipleDigestOutputStream(filePath, baos);

        mdos.write(s.getBytes());
        mdos.flush();

        final String md5 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.MD5.getAlgorithm());
        final String sha1 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA1.getAlgorithm());
        final String sha256 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA256.getAlgorithm());
        final String sha512 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA512.getAlgorithm());

        assertEquals("120ea8a25e5d487bf68b5f7096440019", md5, "Incorrect MD5 sum!");
        assertEquals("afa6c8b3a2fae95785dc7d9685a57835d703ac88", sha1, "Incorrect SHA-1 sum!");
        assertEquals("a8a2f6ebe286697c527eb35a58b5539532e9b3ae3b64d4eb0a46fb657b41562c", sha256, "Incorrect SHA-256 sum!");
        assertEquals("f3bf9aa70169e4ab5339f20758986538fe6c96d7be3d184a036cde8161105fcf53516428fa096ac56247bb88085b0587d5ec8e56a6807b1af351305b2103d74b",
                     sha512,
                     "Incorrect SHA-512 sum!");

        mdos.close();

        logger.debug("MD5:  " + md5);
        logger.debug("SHA1: " + sha1);
        logger.debug("SHA256: " + sha256);
        logger.debug("SHA512: " + sha512);

        Path md5File = filePath.resolveSibling(filePath.getFileName() + EncryptionAlgorithmsEnum.MD5.getExtension());
        Path sha1File = filePath.resolveSibling(filePath.getFileName() + EncryptionAlgorithmsEnum.SHA1.getExtension());
        Path sha256File = filePath.resolveSibling(filePath.getFileName() + EncryptionAlgorithmsEnum.SHA256.getExtension());
        Path sha512File = filePath.resolveSibling(filePath.getFileName() + EncryptionAlgorithmsEnum.SHA512.getExtension());

        assertTrue(Files.exists(md5File), "Failed to create md5 checksum filePath!");
        assertTrue(Files.exists(sha1File), "Failed to create sha1 checksum filePath!");
        assertTrue(Files.exists(sha256File), "Failed to create sha256 checksum filePath!");
        assertTrue(Files.exists(sha512File), "Failed to create sha512 checksum filePath!");

        String md5ReadIn = MessageDigestUtils.readChecksumFile(md5File.toString());
        String sha1ReadIn = MessageDigestUtils.readChecksumFile(sha1File.toString());
        String sha256ReadIn = MessageDigestUtils.readChecksumFile(sha256File.toString());
        String sha512ReadIn = MessageDigestUtils.readChecksumFile(sha512File.toString());

        assertEquals(md5, md5ReadIn, "MD5 checksum filePath contains incorrect checksum!");
        assertEquals(sha1, sha1ReadIn, "SHA-1 checksum filePath contains incorrect checksum!");
        assertEquals(sha256, sha256ReadIn, "SHA-256 checksum filePath contains incorrect checksum!");
        assertEquals(sha512, sha512ReadIn, "SHA-512 checksum filePath contains incorrect checksum!");
    }

    @Test
    void testConcatenatedWrites()
            throws IOException,
                   NoSuchAlgorithmException
    {
        String string = "This is a big fat super long text which has no meaning, but is good for the test.";

        ByteArrayInputStream bais1 = new ByteArrayInputStream(string.getBytes());
        ByteArrayInputStream bais2 = new ByteArrayInputStream(string.getBytes());

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

        logger.debug("Skipped {}/{} bytes.", total, string.getBytes().length);

        while ((len = bais2.read(bytes, 0, size)) != -1)
        {
            mdos.write(bytes, 0, len);

            total += len;
        }

        mdos.flush();

        logger.debug("Original:      {}", string);
        logger.debug("Read:          {}", new String(baos.toByteArray()));

        logger.debug("Read {}/{} bytes.", total, string.getBytes().length);

        mdos.close();

        final String md5 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.MD5.getAlgorithm());
        final String sha1 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA1.getAlgorithm());
        final String sha256 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA256.getAlgorithm());
        final String sha512 = mdos.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA512.getAlgorithm());

        logger.debug("MD5:  {}", md5);
        logger.debug("SHA1: {}", sha1);
        logger.debug("SHA256: {}", sha256);
        logger.debug("SHA512: {}", sha512);

        assertEquals("693188a2fb009bf2a87afcbca95cfcd6", md5, "Incorrect MD5 sum!");
        assertEquals("6ed7c74babd1609cb11836279672ade14a8748c1", sha1, "Incorrect SHA-1 sum!");
        assertEquals("4716861ef13609f87cc9ba9d0f9cba6c1d7894dd21cfca2f965631d6933fffbb", sha256, "Incorrect SHA-256 sum!");
        assertEquals("d87e40392a0749350059a366f3cc2e72a008b5feae3a845528660649545d8388b86ad540498756d9c5eb9365c5940c4146a8f5302d1ed42a253b90f9ec438deb",
                     sha512,
                     "Incorrect SHA-512 sum!");
    }

}
