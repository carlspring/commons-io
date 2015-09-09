# commons-io

This is a simple library for common IO operations.

Currently, it contains:
- An efficient implementation of file moving which is optimized for SSD-s based on the functionality available in JDK 1.7.
  This differs from Apache Commons IO, Guava and Spring's implementations in that it doesn't need to first copy and then delete,
  but instead carries out a real move operation. This also performs a merge operation.
- A RandomInputStream which can be quite useful when you have to generate a number of random bytes.
