package com.example.codigosbasicos.utilities.guava;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.math.BigIntegerMath;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;

/**
 * GUAVA (GOOGLE CORE LIBRARIES FOR JAVA)
 * 
 * //// GUAVA ES UNA LIBRERÍA (GOOGLE CORE LIBRARIES FOR JAVA) DE UTILIDADES BÁSICAS
 * //// SUS MÉTODOS USAN LLAMADOS DEL ESTILO PROGRAMACIÓN FUNCIONAL POR SER MÁS LEGIBLES
 * */
public class GuavaDemo {

	public static void main(String[] args) {
		new GuavaDemo().run();
	}
	
	private void run() {
		System.err.println("--------------------------------------------------- BEGIN:	GUAVA (GOOGLE CORE LIBRARIES FOR JAVA)");
		
		//// 5 FUNCIONALIDADES A PROBAR:
	
		//- Optional, Preconditions & Ordering class
		System.out.println("====================== OPTIONAL, PRECONDITIONS & ORDERING CLASS");
		optionalClassTest();
		preconditionsClassTest();
		orderingClassTest();

		//- Objects & MoreObjects
		System.out.println("====================== OBJECTS & MOREOBJECTS CLASS");
		System.out.println("5 equals 5: " + Objects.equal(new Integer(5), new Integer(5)));
		System.out.println("First non null of [null, 6]: " + MoreObjects.firstNonNull(null, new Integer(6)));
		System.out.println("HashCode generated from some objects: " + Objects.hashCode(new Integer(10), "Cadena", null, new Double(0)));

		//- Range
		System.out.println("====================== RANGE CLASS");
		//create a range [a,b] = { x | a <= x <= b}
		Range<Integer> range1 = Range.closed(0, 9);
		for(int grade : ContiguousSet.create(range1, DiscreteDomain.integers())) {
			System.out.print(grade +" ");
		}
		System.out.println("(1,2,3) is present: " + range1.containsAll(Ints.asList(1, 2, 3)));

		//- LoadingCache
		System.out.println("====================== LOADING CACHE");
		System.out.println("***********************************************************************************************************************");
		System.out.println("**************** GUAVA CACHING FEATURE WAS MOVED TO BackToBasics PROJECT, caching topic ****************");
		System.out.println("***********************************************************************************************************************");
		
		//- String Utilities
		System.out.println("====================== STRING UTILITIES");
		stringUtilitiesTest();
		
		System.err.println("---------------------------------------------------	END: GUAVA (GOOGLE CORE LIBRARIES FOR JAVA)");
	}
	

	private void preconditionsClassTest() {
		try {
			Preconditions.checkArgument(-3 > 0.0, "Illegal Argument passed: Negative value %s.", -3);
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

		try {
			Preconditions.checkNotNull(new Integer(5), "Illegal Argument passed: First parameter is Null.");
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
		}

		try {
			int[] data = {1,2,3,4,5};
			Preconditions.checkElementIndex(6, data.length, "Illegal Argument passed: Invalid index.");
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
		}
	}


	private void optionalClassTest() {
		Optional<Integer> intA = Optional.absent();
		Optional<Integer> intB = Optional.fromNullable(null);
		Optional<Integer> intC = Optional.of(new Integer(10));

		List<Optional<Integer>> list = new ArrayList<>();
		list.add(intA); list.add(intB); list.add(intC);

		System.out.println(sum(list));
	}

	private int sum(List<Optional<Integer>> list) {
		int res = 0;

		//1 option
		for (int i : Optional.presentInstances(list)) {
			res += i;
		}

		for (Optional<Integer> op : list) {	
			//2 option
			//res += (int) op.or(Optional.of(0)).get();

			if(op.isPresent()) {
				op = op.transform((a) -> {
					return a * 2;
				});

				//3 option (usando el isPresent)
				//res += op.get();
			}
		}

		return res;
	}


	private void orderingClassTest() {

		List<Integer> numbers = new ArrayList<Integer>();

		numbers.add(new Integer(5));
		numbers.add(new Integer(2));
		numbers.add(new Integer(15));
		numbers.add(new Integer(51));
		numbers.add(new Integer(53));
		numbers.add(new Integer(35));
		numbers.add(new Integer(45));
		numbers.add(new Integer(32));
		numbers.add(new Integer(43));
		numbers.add(new Integer(16));

		Ordering ordering = Ordering.natural();
		System.out.println("Input List: ");
		System.out.println(numbers);		

		Collections.sort(numbers, ordering);
		System.out.println("Sorted List: ");
		System.out.println(numbers);

		System.out.println("======================");
		System.out.println("List is sorted: " + ordering.isOrdered(numbers));
		System.out.println("Minimum: " + ordering.min(numbers));
		System.out.println("Maximum: " + ordering.max(numbers));

		Collections.sort(numbers, ordering.reverse());
		System.out.println("Reverse: " + numbers);

		numbers.add(null);
		System.out.println("Null added to Sorted List: ");
		System.out.println(numbers);		

		Collections.sort(numbers, ordering.nullsFirst());
		System.out.println("Null first Sorted List: ");
		System.out.println(numbers);
		System.out.println("======================");

		List<String> names = new ArrayList<String>();

		names.add("Ram");
		names.add("Shyam");
		names.add("Mohan");
		names.add("Sohan");
		names.add("Ramesh");
		names.add("Suresh");
		names.add("Naresh");
		names.add("Mahesh");
		names.add(null);
		names.add("Vikas");
		names.add("Deepak");

		System.out.println("Another List: ");
		System.out.println(names);

		Collections.sort(names, ordering.nullsFirst().reverse());
		System.out.println("Null first then reverse sorted list: ");
		System.out.println(names);
	}
	
	
	private void stringUtilitiesTest() {
		
		//Joiner
		System.out.println(Joiner.on(",")
				.skipNulls()//.useForNull("NULO")
				.join(Arrays.asList(1,2,3,4,5,null,6)));
		//Java version: String.join(",", Arrays.asList("1","2","3","4","5",null,"6")) .. does not have skip
		
		//Splitter
		System.out.println(Splitter.on(',')
				.trimResults()
				.omitEmptyStrings()
				.split("the ,quick, ,brown, fox, jumps, over, the, lazy, little dog."));
		
		
		Strings.isNullOrEmpty("");
		//Java: "text".isEmpty()
		
		
		//CharMatcher
		System.out.println(CharMatcher.inRange('0','9').retainFrom("mahesh123"));   // only the digits
		System.out.println(CharMatcher.inRange('0','9').or(CharMatcher.inRange('a','z')).retainFrom("mahesh123"));
		// trim whitespace at ends, and replace/collapse whitespace into single spaces
		System.out.println(CharMatcher.breakingWhitespace().trimAndCollapseFrom("     Mahesh     Parashar ", ' '));
		System.out.println(CharMatcher.inRange('0','9').replaceFrom("mahesh123", "*"));  // star out all digits
		// eliminate all characters that aren't digits or lower-case
		System.out.println(":" + CharMatcher.inRange('0','9').or(CharMatcher.inRange('a','z')).removeFrom("mahesh123"));
		// check if a string is Upper/Lower case... Java just have Character.isUpperCase()
		System.out.println("TEXTO is uppercase?: " + CharMatcher.inRange('A','Z').matchesAllOf("TEXTO"));
		System.out.println("TEXto is uppercase?: " + CharMatcher.inRange('A','Z').matchesAllOf("TEXto"));
		System.out.println("texto is lowercase?: " + CharMatcher.inRange('a','z').matchesAllOf("texto"));
		
		
		//CaseFormat (cambia de un format case a otro)
		System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "test-data"));
		System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));
		System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "test_data"));
		System.out.println(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, "TestData"));
		System.out.println(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "test_data"));
		
		
		//Primitives
		//As primitive types of Java cannot be used to pass in generics or in collections as input, 
		//Guava provided a lot of Wrapper Utilities classes to handle primitive types as Objects.
		int[] intArray = {1,2,3,4,5,6,7,8,9};
		//convert array of primitives to array of objects
		List<Integer> objectArray = Ints.asList(intArray);
		System.out.println(objectArray.toString());
		//convert array of objects to array of primitives
		intArray = Ints.toArray(objectArray);
		//Bytes.asList(...)    //Booleans.asList(...)
		
		
		//Math Utilities
		//com.google.common.math package
		System.out.println(IntMath.divide(100, 5, RoundingMode.UNNECESSARY));
		System.out.println(IntMath.divide(100, 3, RoundingMode.HALF_UP));
		System.out.println("Log2(2): " + IntMath.log2(2, RoundingMode.HALF_EVEN));
		System.out.println("Log10(10): " + IntMath.log10(10, RoundingMode.HALF_EVEN));
		System.out.println("sqrt(100): " + IntMath.sqrt(IntMath.pow(10,2), RoundingMode.HALF_EVEN));
		System.out.println("Log2(2): " + BigIntegerMath.log2(new BigInteger("2"), RoundingMode.HALF_EVEN));
		
	}
	

}
