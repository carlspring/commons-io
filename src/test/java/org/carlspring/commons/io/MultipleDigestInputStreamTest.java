package org.carlspring.commons.io;

import org.carlspring.commons.encryption.EncryptionAlgorithmsEnum;
import org.carlspring.commons.http.range.ByteRange;
import org.carlspring.commons.io.reloading.FSReloadableInputStreamHandler;
import org.carlspring.commons.io.reloading.ReloadableInputStreamHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author mtodorov
 */
class MultipleDigestInputStreamTest
{

    private static final Logger logger = LoggerFactory.getLogger(MultipleDigestInputStreamTest.class);

    @BeforeEach
    void setUp()
            throws Exception
    {
        Path testResourcesDir = Paths.get("target/test-resources").toAbsolutePath();
        if (Files.notExists(testResourcesDir))
        {
            Files.createDirectories(testResourcesDir);
        }
    }

    @Test
    void testRead()
            throws IOException,
                   NoSuchAlgorithmException
    {
        String s = "This is a big fat super long text which has no meaning, but is good for the test.";

        ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
        MultipleDigestInputStream mdis = new MultipleDigestInputStream(bais);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int size = 16;
        byte[] bytes = new byte[size];
        int len;

        while ((len = mdis.read(bytes, 0, size)) != -1)
        {
            baos.write(bytes, 0, len);
        }

        baos.flush();

        logger.debug(new String(baos.toByteArray()));

        final String md5 = mdis.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.MD5.getAlgorithm());
        final String sha1 = mdis.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA1.getAlgorithm());
        final String sha256 = mdis.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA256.getAlgorithm());
        final String sha512 = mdis.getMessageDigestAsHexadecimalString(EncryptionAlgorithmsEnum.SHA512.getAlgorithm());

        assertEquals("693188a2fb009bf2a87afcbca95cfcd6", md5, "Incorrect MD5 sum!");
        assertEquals("6ed7c74babd1609cb11836279672ade14a8748c1", sha1, "Incorrect SHA-1 sum!");
        assertEquals("4716861ef13609f87cc9ba9d0f9cba6c1d7894dd21cfca2f965631d6933fffbb", sha256, "Incorrect SHA-256 sum!");
        assertEquals("d87e40392a0749350059a366f3cc2e72a008b5feae3a845528660649545d8388b86ad540498756d9c5eb9365c5940c4146a8f5302d1ed42a253b90f9ec438deb",
                     sha512,
                     "Incorrect SHA-512 sum!");

        logger.debug("MD5:  {}", md5);
        logger.debug("SHA1: {}", sha1);
        logger.debug("SHA1: {}", sha256);
        logger.debug("SHA1: {}", sha512);
    }

    @Test
    void testReloading()
            throws IOException
    {
        Path path = Paths.get("target/test-resources/test-stream-reloading.txt").toAbsolutePath();

        try (OutputStream fos = Files.newOutputStream(path))
        {
            fos.write("This is a test.\n".getBytes());
            fos.flush();
        }

        ByteRange byteRange1 = new ByteRange(0L, 10L);
        ByteRange byteRange2 = new ByteRange(11L, 21L);

        List<ByteRange> byteRanges = Arrays.asList(byteRange1, byteRange2);

        ReloadableInputStreamHandler handler = new FSReloadableInputStreamHandler(path);
        MultipleDigestInputStream mdis = new MultipleDigestInputStream(handler, byteRanges);
        mdis.setLimit(1);

        long len = 0L;
        while (mdis.read() != -1)
        {
            len++;
        }

        assertEquals(1L, len, "Failed to limit byte range!");

        mdis.reload();
        mdis.setLimit(3);

        len = 0;

        while (mdis.read() != -1)
        {
            len++;
        }

        assertEquals(2L, len, "Failed to limit byte range!");
    }

}
