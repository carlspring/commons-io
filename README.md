# commons-io

This is a simple library for common IO operations.

Currently, it contains:
- A MultipleDigestInputStream which allows generating multiple MessageDigest-s and updating them while reading.
- A MultipleDigestOutputStream which allows generating multiple MessageDigest-s and updating them while writing.
- A basic implementation of file moving which is optimized for SSD-s based on the functionality available in JDK 1.7.
