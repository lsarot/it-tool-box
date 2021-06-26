package com.example.demospringsec;


/** PARA ENTENDER EL TEMA:
 * https://www.websecurity.digicert.com/en/in/security-topics/what-is-ssl-tls-https
 * */
/*
 - Básicamente son usados para una página web.
 		.Un certificado DigiCert anual están en 400$, 995$, 1500$ o 2000$ (https://www.websecurity.digicert.com/ssl-certificate?inid=infoctr_buylink_sslhome)
 		.Unos baratos https://www.ssldragon.com/
 		.FREE CERTIFICATE CA (WIDELYTRUSTED BY BROWSERS, but only domain is certified, not ie. EV certificate) https://letsencrypt.org/
 		.O podemos emitir nuestro propio certificado.
 
 - Para una app que trabaja con su propia API, puedo cifrar los mensajes que yo quiera directamente usando la encriptación que considere adecuada.
 		.Evitamos perjudicar la latencia, producto de cifrar/descifrar toda la comunicación.
 
 * */
/**
 * TLS substitute SSL, the last one has a large number of weaknesses
 * 		QUIC (Google, Quick UDP Internet Connections), Tink (Google) y s2n (Amazon, singnal2noise)... Los más nuevos
 * 		In order: SSL, SSLv2, SSLv3, 
 * 								SSLv3.1=TLSv1, TLSv1.1, TLSv1.2, TLSv1.3 (1996, 2006, 2008, 2017 respectively)
 * 	
 * Son certificados, emitidos por una CA, y al ser configurados en el servidot, se activa la conexión Https, con el protocolo SSL o TLS sobre el TCP.
 * */
/* *** TLS CHEAT SHEET
 * https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Protection_Cheat_Sheet.html
 * ----------------
 * ON THE SERVER:
 * Only Support Strong Protocols
		General purpose web applications should only support TLS 1.2 and TLS 1.3, with all other protocols disabled. Where it is known that a web server must support legacy clients with unsupported an insecure browsers (such as Internet Explorer 10), it may be necessary to enable TLS 1.0 to provide support.
		Where legacy protocols are required, the "TLS_FALLBACK_SCSV" extension should be enabled in order to prevent downgrade attacks against clients.
 * Only support strong ciphers
		There are a large number of different ciphers (or cipher suites) that are supported by TLS, that provide varying levels of security. Where possible, only GCM ciphers should be enabled. However, if it is necessary to support legacy clients, then other ciphers may be required.
		At a minimum, the following types of ciphers should always be disabled: Null ciphers, Anonymous ciphers, EXPORT ciphers
* Use Strong Diffie-Hellman Parameters
		Where ciphers that use the ephemeral Diffie-Hellman key exchange are in use (signified by the "DHE" or "EDH" strings in the cipher name) sufficiently secure Diffie-Hellman parameters (at least 2048 bits) should be used
		The following command can be used to generate 2048 bit parameters:
		$ openssl dhparam 2048 -out dhparam2048.pem
* Disable Compression
		TLS compression should be disabled in order to protect against a vulnerability (nicknamed CRIME) which could potentially allow sensitive information such as session cookies to be recovered by an attacker.
* Patch Cryptographic Libraries
		As well as the vulnerabilities in the SSL and TLS protocols, there have also been a large number of historic vulnerability in SSL and TLS libraries, with Heartbleed being the most well known. As such, it is important to ensure that these libraries are kept up to date with the latest security patches.
* Use Strong Keys and Protect Them
* Use Strong Cryptographic Hashing Algorithms
* Use Correct Domain Names
* Carefully Consider the use of Wildcard Certificates
* Use an Appropriate Certification Authority for the Application's User Base
* Use CAA Records to Restrict Which CAs can Issue Certificates
* Always Provide All Needed Certificates
* Re-Consider the use of Extended Validation Certificates
* -----------------
* ON APPLICATION:
* Use TLS For All Pages
* Do Not Mix TLS and Non-TLS Content
* Use the "Secure" Cookie Flag
* Prevent Caching of Sensitive Data:   Cache-Control: no-cache, no-store, must-revalidate ; Pragma: no-cache ; Expires: 0
* Use HTTP Strict Transport Security
* Re-Consider Using Public Key Pinning
* Consider the use of Client-Side Certificates (in non public scenarios, not a webpage)
 */
/** CONFIGURE TLS ON SPRING BOOT'S TOMCAT:

You may experience an SSL handshake error due to the default ciphers that spring boot includes. It is recommended that you define a set of ciphers. We had a similar issue, and the way we fixed it was by using SSLScan on the caller and then scanning our system to see if there were any matches. This lead us to find out that there were no matches and helped us define a list of ciphers we should support.
Using SSLScan these are the default ciphers spring boot will use: (esto lo ejecutaron en un Spring Boot 1.x, quizás ahora usa otras por defecto)
Preferred  TLSv1.2  128 bits  ECDHE-RSA-AES128-GCM-SHA256   Curve P-256 DHE 256
Accepted  TLSv1.2  128 bits  ECDHE-RSA-AES128-SHA256             Curve P-256 DHE 256
Accepted  TLSv1.2  128 bits  ECDHE-RSA-AES128-SHA                    Curve P-256 DHE 256
Accepted  TLSv1.2  128 bits  DHE-RSA-AES128-GCM-SHA256        DHE 1024 bits
Accepted  TLSv1.2  128 bits  DHE-RSA-AES128-SHA256                  DHE 1024 bits
Accepted  TLSv1.2  128 bits  DHE-RSA-AES128-SHA                         DHE 1024 bits
		Lista de ciphers:  https://docs.microsoft.com/es-es/windows/win32/secauthn/tls-cipher-suites-in-windows-10-v1709
  				Para la config de SpringB NOTAR QUE LOS DE ARRIBA HAY QUE TRANSFORMARLOS, ie. ECDHE-RSA-AES128-GCM-SHA256   A   TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
-------------------
Verify SSL usage by command line:
		openssl s_client -connect serverAddress:port     (openssl s_client -connect www.google.com:443)
Which outputs:
		SSL-Session:
		Protocol  : TLSv1.2
		Cipher    : ECDHE-RSA-AES256-SHA384
-------------------
In Spring Boot config file (.properties or .yml) do the following:

(ver proyecto SpringEnableHttpsSelfSignedCert, application-ssl.properties)
 * */
/* IN APACHE WEB SERVER:
 * En fichero config SSL, Locate SSLProtocol line and add +TLSv1.3 at the end of the line
SSLProtocol -all +TLSv1.2 +TLSv1.3     //Ex: the following would allow TLS 1.2 and TLS 1.3
Check protocols used:
	https://gf.dev/tls-test
Or on Chrome dev tools, Security tab.
 * */
public class SSL_TLS_Https {}


