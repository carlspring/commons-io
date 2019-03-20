# commons-io

[![Master Build Status][build-badge]][build-link]
[![Known Vulnerabilities][vulnerabilities-badge]][vulnerabilities-link]
[![Maven Central Release][release-badge]][release-link]

This is a simple library for common IO operations.

Currently, it contains:
- An efficient implementation of file moving ([FileUtils#moveDirectory](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/FileUtils.java)) which is optimized for SSD-s based on the functionality available in JDK 1.7.
  This differs from Apache Commons IO, Guava and Spring's implementations in that it doesn't need to first copy and then delete,
  but instead carries out a real move operation. This also performs a merge operation.
- A RandomInputStream which can be quite useful when you have to generate a number of random bytes.
- [MultipleDigestInputStream](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/MultipleDigestInputStream.java)/[MultipleDigestOutputStream](https://github.com/carlspring/commons-io/blob/master/src/main/java/org/carlspring/commons/io/MultipleDigestOutputStream.java) which allow you to calculate digests when you're done reading/writing streams, hence avoiding having to re-read the stream.

[build-link]: https://jenkins.carlspring.org/blue/organizations/jenkins/opensource%2Fcommons-io/activity?branch=master
[build-badge]: https://jenkins.carlspring.org/buildStatus/icon?job=opensource%2Fcommons-io%2Fmaster
[vulnerabilities-link]: https://snyk.io/test/github/carlspring/commons-io/
[vulnerabilities-badge]: https://snyk.io/test/github/carlspring/commons-io/badge.svg
[release-link]: http://repo2.maven.org/maven2/org/carlspring/commons/commons-io/
[release-badge]: https://img.shields.io/maven-central/v/org.carlspring.commons/commons-io.svg
