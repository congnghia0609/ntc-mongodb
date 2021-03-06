/*
 * Copyright 2018 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.mongodb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Feb 7, 2018
 */
public class SSLContextUtil {
    private static final String PROTOCOL = "TLS";
	private static final String DEFAULT_ALGORITHM = "SunX509";

	private static final Logger log = LoggerFactory.getLogger(SSLContextUtil.class);

	/**
	 * Creates a new SSL context using the JVM default trust managers and the certificates in the given PKCS12 file.
	 *
	 * @param pathToPKCS12File the path to a PKCS12 file that contains the client certificate
	 * @param keystorePassword the password to read the PKCS12 file; may be {@code null}
	 *
	 * @return an SSL context configured with the given client certificate and the JVM default trust managers
     * @throws java.security.KeyStoreException KeyStoreException
     * @throws java.security.NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws java.security.cert.CertificateException CertificateException
     * @throws java.security.UnrecoverableKeyException UnrecoverableKeyException
     * @throws java.security.KeyManagementException KeyManagementException
     * @throws java.io.IOException IOException
	 */
	public static SSLContext createDefaultSSLContext(final String pathToPKCS12File, final String keystorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException, IOException {
		final FileInputStream keystoreInputStream = new FileInputStream(pathToPKCS12File);
		try {
			return createDefaultSSLContext(keystoreInputStream, keystorePassword);
		} finally {
			try {
				keystoreInputStream.close();
			} catch (IOException e) {
				log.error("Failed to close keystore input stream.", e);
			}
		}
	}

	/**
	 * Creates a new SSL context using the JVM default trust managers and the certificates in the given PKCS12 InputStream.
	 *
	 * @param keystoreInputStream a PEM file that contains the client certificate
	 * @param keystorePassword the password to read the PEM file; may be {@code null}
	 *
	 * @return an SSL context configured with the given client certificate and the JVM default trust managers
     * @throws java.security.KeyStoreException KeyStoreException
     * @throws java.security.NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws java.security.cert.CertificateException CertificateException
     * @throws java.security.UnrecoverableKeyException UnrecoverableKeyException
     * @throws java.security.KeyManagementException KeyManagementException
     * @throws java.io.IOException IOException
	 */
	public static SSLContext createDefaultSSLContext(final InputStream keystoreInputStream, final String keystorePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException, IOException {
		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		final char[] password = keystorePassword != null ? keystorePassword.toCharArray() : null;

		keyStore.load(keystoreInputStream, password);

		return createDefaultSSLContext(keyStore, keystorePassword != null ? keystorePassword.toCharArray() : null);
	}

	/**
	 * Creates a new SSL context using the JVM default trust managers and the certificates in the given keystore.
	 *
	 * @param keyStore A {@code KeyStore} containing the client certificates to present during a TLS handshake; may be
	 * {@code null} if the environment does not require TLS. The {@code KeyStore} should be loaded before being used
	 * here.
	 * @param keyStorePassword a password to unlock the given {@code KeyStore}; may be {@code null}
	 *
	 * @return an SSL context configured with the certificates in the given keystore and the JVM default trust managers
     * @throws java.security.KeyStoreException KeyStoreException
     * @throws java.security.NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws java.security.UnrecoverableKeyException UnrecoverableKeyException
     * @throws java.security.KeyManagementException KeyManagementException
	 */
	public static SSLContext createDefaultSSLContext(final KeyStore keyStore, final char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");

		if (algorithm == null) {
			algorithm = DEFAULT_ALGORITHM;
		}

		if (keyStore.size() == 0) {
			throw new KeyStoreException("Keystore is empty; while this is legal for keystores in general, APNs clients must have at least one key.");
		}

		//final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
		//trustManagerFactory.init(keyStore);

		final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
		keyManagerFactory.init(keyStore, keyStorePassword);

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };
        
		final SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
		//sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);

		return sslContext;
	}
}
