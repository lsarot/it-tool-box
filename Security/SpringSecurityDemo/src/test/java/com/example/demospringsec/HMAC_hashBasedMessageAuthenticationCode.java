package com.example.demospringsec;


/**
 * HMAC(Hash-based message authentication code)
 * is a message authentication code that uses a cryptographic hash function such as SHA-256, SHA-512 and a secret key known as a cryptographic key.
 * HMAC is more secure than any other authentication codes as it contains Hashing as well as MAC.
 * */
public class HMAC_hashBasedMessageAuthenticationCode {

	/*
	Trabaja similar a JWT, o más bien, JWT puede entregar la signature como un HMAC del header y payload (JWT puede usar otro tipo de encriptado de la signature).

	La signature esconde (encrypted) el hash del mensaje, con una private key se descifra el hash del mensaje y se compara con lo que me envían.
	Si la comunicación es entre 2 servidores y hay mutua confianza, se pueden compartir la key y validar que el mensaje enviado es integro (mismo hash) y auténtico (porque no todos tienen la key).

	The short answer is "HMAC provides digital signatures using symmetric keys instead of PKI". 
	Essentially, if you don't want to deal with complexities of public/private keys, root of trust and certificate chains, you can still have reliable digital signature with HMAC.
	HMAC relies on symmetric key cryptography and pre-shared secrets instead of private/public pairs. The downside is the same as with symmetric key cryptography in general - you now need to worry about distribution and protection of your secret keys.
	
	ES UNA ALTERNATIVA MENOS SEGURA Y MENOS COMPLEJA QUE ie. RSA (Two-Way, asymmetric encription (public/private keys).
	
	El mensaje va con su Firma Digital, sólo el que tiene la key podra leerlo y saber que el contenido es auténtico, claro si se filtra la key, ya no funciona.
	 **/
	
	
	/** ANÁLISIS:
	 * Con RSA se hace muy pesado cifrar/descifrar todo el contenido de un mensaje,
	 * entonces le damos apoyo con AES (buen cifrado de tipo simétrico), encriptando la key AES con la public-key RSA y el contenido se encripta con AES que es más ligero y rápido.
	 * A la otra parte (server) se le envía mensaje encriptado con AES y una clave AES encriptada con public-key RSA. 
	 * O al revés si es el servidor quien responde, se encripta la key AES con la private y del otro lado se usa la public ? NOOO
	 * Si encripto la respuesta del servidor con la private, cualquiera con la public podrá leerla.. el servidor encripta la respuesta con la AES que le envió el cliente. 
	 * 
	 * 
	 * Si un mensaje no debe ser encriptado, digamos una URL o un token (JWT), pero queremos asegurar que quien lo envía es de confianza y que el mensaje está integro.
	 * Usamos algo tipo HMAC, donde enviamos el hash (del mensaje) encriptado, y el mensaje visible.
	 * Es una firma digital que asegura que quien emite el mensaje es de confianza (porque tiene la key del hash) y que su contenido no fue modificado (hash del contenido coincide con hash encriptado).
	 * 
	 * JWT parece diseñado pensando en como funciona HMAC,
	 * pero aquí también se cifra la signature con RSA u otros algoritmos.
	 * */
	
}
