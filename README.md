# MSCS535 Assignment 3: Application Security

This repository contains my submission for **MSCS535 Assignment 3**, focused on **application-layer security (OSI Layer 7)** in Java. The project includes:

- a servlet security filter that applies common Layer 7 protections
- a One-Time Pad encryption/decryption demo for the required plaintext: `MY NAME IS UNKNOWN`

## Assignment Goal

The purpose of this assignment is to demonstrate how security can be enforced at the application layer. This submission shows both:

- practical web application protections through a Java servlet filter
- a cryptography example using the One-Time Pad technique

## Project Files 

### `SecurityFilter.java`

This file implements a Java Servlet `Filter` that protects incoming HTTP requests before they reach the application.

Implemented protections:

- Restricts requests to `GET` and `POST`
- Rejects oversized requests above `8 KB`
- Validates request parameters using a safe-input pattern
- Applies per-client rate limiting of `60` requests per minute
- Adds security headers such as:
  - `X-Content-Type-Options`
  - `X-Frame-Options`
  - `Content-Security-Policy`
  - `Referrer-Policy`
  - `Permissions-Policy`
  - `Cache-Control`
  - `Strict-Transport-Security`

This demonstrates how application-layer defenses can reduce the risk of attacks such as injection, clickjacking, malicious input submission, and abusive request flooding.

### `OneTimePadDemo.java`

This file demonstrates One-Time Pad encryption and decryption using the exact assignment text:

`MY NAME IS UNKNOWN`

The program:

- generates a random key of equal length
- encrypts the plaintext
- decrypts the ciphertext using the same key
- prints all values to verify correctness

## How to Compile and Run

### One-Time Pad Demo

Compile:

```bash
javac OneTimePadDemo.java
```

Run:

```bash
java OneTimePadDemo
```

Expected behavior:

- the program prints the plaintext
- a randomly generated key is displayed
- the ciphertext is produced
- the decrypted text matches the original plaintext

### Security Filter

`SecurityFilter.java` is intended for a servlet-based Java web application and should be deployed in a servlet container such as:

- Apache Tomcat
- Jetty
- GlassFish

To use it:

1. Add the filter class to your web application.
2. Register it in `web.xml` or through annotation-based configuration.
3. Run the application in a servlet container.

Note: this file depends on the `jakarta.servlet` API, so it is not meant to be run as a standalone Java program.

## Security Concepts Demonstrated

This assignment highlights several important Layer 7 security practices:

- **Input validation** to reject unexpected or potentially harmful user input
- **HTTP security headers** to harden browser behavior
- **Rate limiting** to reduce abuse and denial-of-service style traffic bursts
- **Request size limits** to control oversized payloads
- **Method restrictions** to reduce the attack surface

Together, these controls show how application-layer defenses protect web applications beyond lower network layers.

## One-Time Pad Summary

The One-Time Pad is a symmetric encryption technique that is secure when:

- the key is truly random
- the key is the same length as the message
- the key is used only once
- the key remains secret

In this implementation, the plaintext and key are processed using a defined alphabet of uppercase letters and spaces. Encryption and decryption are performed character by character, and the original plaintext is recovered successfully.

## Conclusion

This project has the following:

- a Java-based Layer 7 security implementation through `SecurityFilter.java`
- a working One-Time Pad demo for `MY NAME IS UNKNOWN`
- clear instructions for building and running the included code
