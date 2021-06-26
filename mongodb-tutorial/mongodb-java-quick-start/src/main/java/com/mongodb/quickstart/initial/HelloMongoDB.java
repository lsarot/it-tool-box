package com.mongodb.quickstart.initial;

import java.math.BigDecimal;

/**
 * https://www.mongodb.com/blog/channel/quickstart
 * */
public class HelloMongoDB {

	public static void main(String[] args) {
		System.out.println("Hello MongoDB!");

		//// OTROS TEMAS APARTES:

		//----------------------------- HOW TO STORE BIG NUMBERS IN MONGODB ?

		//usar NumberDecimal("9823.1297") ... pass in as a String
		//map in Java to BigDecimal object, these doesn't lose precision

		System.out.println("0.1 * 0.2:");
		System.out.println(0.1 * 0.2);
		System.out.println(BigDecimal.valueOf(0.1).multiply(BigDecimal.valueOf(0.2)));

		//----------------------------- BSON ObjectId data type

		//the ObjectId datatype is automatically generated as a unique document identifier if no other identifier is provided.
		//in its current implementation, it is a 12-byte hexadecimal value. This 12-byte configuration is smaller than a typical universally unique identifier (UUID), which is, typically, 128-bits.
		//    4-byte value representing the seconds since the Unix epoch,        5-byte random value, and        3-byte counter, starting with a random value.

		//Binary JSON (BSON)
		//Many programming languages have JavaScript Object Notation (JSON) support or similar data structures. MongoDB uses JSON documents to store records. However, behind the scenes, MongoDB represents these documents in a binary-encoded format called BSON. BSON provides additional data types and ordered fields to allow for efficient support across a variety of languages. One of these additional data types is ObjectId.

		//anteriormente se usaban 5 bytes entre machine-identifier y process-id, pero con el uso de VMs, no se garantizaba la unicidad ya que estas tenían mismas direcciones MAC y procesos que arrancaban en el mismo orden.

		//----------------------------- BSON Date data type

		//is a 64-bit integer. It represents the number of milliseconds since the Unix epoch
		//usar Date, dejar timestamp para uso interno de MongoDB

		/*var newDate = new Date();  
	        > newDate;
			ISODate("2020-05-11T20:14:14.796Z")

			> newDate.toString();
			Mon May 11 2020 13:14:14 GMT-0700 (Pacific Daylight Time)
		 */

	}
}
