# commons-io

[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=opensource/commons-io/master)](https://dev.carlspring.org/jenkins/blue/organizations/jenkins/opensource%2Fcommons-io/activity?branch=master)

[![Known Vulnerabilities](https://snyk.io/test/github/carlspring/commons-io/badge.svg)](https://snyk.io/test/github/carlspring/commons-io/)

This is a simple library for common IO operations.

Currently, it contains:
- An efficient implementation of file moving ([FileUtils#moveDirectory](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/FileUtils.java)) which is optimized for SSD-s based on the functionality available in JDK 1.7.
  This differs from Apache Commons IO, Guava and Spring's implementations in that it doesn't need to first copy and then delete,
  but instead carries out a real move operation. This also performs a merge operation.
- A RandomInputStream which can be quite useful when you have to generate a number of random bytes.
- [MultipleDigestInputStream](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/MultipleDigestInputStream.java)/[MultipleDigestOutputStream](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/MultipleDigestOutputStream.java) which allow you to calculate digests when you're done reading/writing streams, hence avoiding having to re-read the stream.
