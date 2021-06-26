package com.example.codigosbasicos.caching.leveldb.leveldb_javaport;

import org.iq80.leveldb.*;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/** LevelDB Java port
 * https://github.com/dain/leveldb
 * 
 * Both this one and the original build, uses the same API, so method calls are the same!.. No need to show anything else!
 * La diferencia es:
 * import static org.iq80.leveldb.impl.Iq80DBFactory.*;
 * en lugar de:
 * import static org.fusesource.leveldbjni.JniDBFactory.*;
 * 
 * 
 * LevelDB get it’s name for the way it stores the data into different levels. Data is first pushed into the log level and as time goes by the data is moved into deeper levels. Here is an idea of how the levels would look.

    Log: Max size of 4MB, then flushed into a set of Level 0 SST files
    Level 0: Max of 4 SST files, then one file compressed into Level 1
    Level 1: Max total size of 10MB, then one file compressed into Level 2
    Level 2: Max total size of 100MB, then one file compressed into Level 3
    Level 3+: Max total size of 10 x previous level, then one file compressed into next level
    
    All data that is put into LevelDB is automatically compressed by Google’s Snappy compression library to keep the size of your database down. Snappy doesn’t optimize for compression size but it optimizes for speed.
 * */
public class LevelDBCache {

	private DB db = null;
	private static final String pathToStore = "example-leveldb-javaport";
	
	public static void main(String[] args) throws IOException {
		new LevelDBCache().run();
	}

	private void run() throws IOException {
		try {
			openingAndClosingTheDatabase();
			
			
			dummiesPerformanceTest();
			// Java port, 800MB ram con o sin compresión activada, 80seg
			// native build, 150MB ram con o sin compresión activada, 90seg
					// compression 50MB disco, wo/compression 200MB
			// RocksDB, 70MB ram, 70MB disco, 200seg (algo de config y ampliando buffer y cache size, sino 400seg) (180seg sin compression)
			
			
			puttingGettingDeletingKeyValues();
			batchBulkAtomicOperations();
			//iteratingKeyValues(); //quite expensive!
			//workingAgainstDatabaseSnapshot();
			
			
			//Getting approximate sizes
			long[] sizes = db.getApproximateSizes(new Range(bytes("a"), bytes("k")), new Range(bytes("k"), bytes("z")));
			System.out.println("Size: "+sizes[0]+", "+sizes[1]);
			
			//Getting database status
			String stats = db.getProperty("leveldb.stats");
			System.out.println(stats);
			
			
			//Using a memory pool to make native memory allocations more efficient
			//JniDBFactory.pushMemoryPool(1024 * 512);
			try {
			    // .. work with the DB in here
			} finally {
			    //JniDBFactory.popMemoryPool();
			}
			
			
			//Repairing a database
			//options = new Options();
			//factory.repair(new File(pathToStore), options);
			
			
			//Destroying a database
			//Options options = new Options();
			//factory.destroy(new File(pathToStore), options);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Make sure you close the db to shutdown the database and avoid resource leaks.
			db.close();
		}
	}

	
	private void openingAndClosingTheDatabase() throws IOException {
		
		//for using a custom comparator.. not mandatory!
		DBComparator comparator = new DBComparator(){
		    public int compare(byte[] key1, byte[] key2) {
		        return new String(key1).compareTo(new String(key2));
		    }
		    public String name() {
		        return "simple";
		    }
		    public byte[] findShortestSeparator(byte[] start, byte[] limit) {
		        return start;
		    }
		    public byte[] findShortSuccessor(byte[] key) {
		        return key;
		    }
		};
		
		Logger logger = new Logger() {
			public void log(String message) {
				System.out.println(message);
			}
		};
		
		Options options = new Options();
		//options.maxOpenFiles(.comparator.); //default 1000
		//options.blockSize(..); //default 4*1024
		//options.comparator(comparator);
		options.compressionType(CompressionType.NONE); //disabling compression
		options.cacheSize(100<<20); // 100MB cache
		options.writeBufferSize(8<<20); //default 4MB.. 8MB
		//options.paranoidChecks(true);
		//options.createIfMissing(true);
		options.logger(logger);
		options.createIfMissing(true);
		db = factory.open(new File(pathToStore), options);
	}

	
	private void puttingGettingDeletingKeyValues() {
		db.put(bytes("Tampa"), bytes("rocks"));
		String value = asString(db.get(bytes("Tampa")));
		System.out.println(value);
		db.delete(bytes("Tampa"));
	}
	
	
	private void batchBulkAtomicOperations() throws IOException {
		WriteBatch batch = db.createWriteBatch();
		try {
		  batch.delete(bytes("Denver"));
		  batch.put(bytes("Tampa"), bytes("green"));
		  batch.put(bytes("London"), bytes("red"));

		  db.write(batch);
		} finally {
		  // Make sure you close the batch to avoid resource leaks.
		  batch.close();
		}
	}

	
	private void iteratingKeyValues() throws IOException {
		DBIterator iterator = db.iterator();
		try {
		  for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
		    String key = asString(iterator.peekNext().getKey());
		    String value = asString(iterator.peekNext().getValue());
		    System.out.println(key+" = "+value);
		  }
		  //or
		  iterator.seekToFirst();
		  iterator.forEachRemaining(e -> {
			  String key = asString(e.getKey());
			  String value = asString(e.getValue());
			  System.out.println(key+" = "+value);
		  });
		} finally {
		  // Make sure you close the iterator to avoid resource leaks.
		  iterator.close();
		}
	}
	
	
	private void workingAgainstDatabaseSnapshot() throws IOException {
		db.put(bytes("a"),bytes("1"));
		Snapshot snapshot = db.getSnapshot();
		ReadOptions ro = new ReadOptions().snapshot(snapshot);
		
		db.put(bytes("a"),bytes("2"));
		System.out.println("from snapshot -> " + asString(db.get(bytes("a"), ro))); // returns 1
		System.out.println("from current -> " + asString(db.get(bytes("a")))); // returns 2
		
		
		db.put(bytes("newKey"), bytes("newValue"));
		try {
			// All read operations will now use the same consistent view of the data.
			
			DBIterator iterator = db.iterator(ro);//we wont see newKey-newValue as long as we are using a snapshot!
			iterator.seekToFirst();
			iterator.forEachRemaining(e -> {
				  String key = asString(e.getKey());
				  String value = asString(e.getValue());
				  System.out.println(key+" = "+value);
			  });
			
			//... = db.get(bytes("Tampa"), ro);
			//...
		} finally {
			// Make sure you close the snapshot to avoid resource leaks.
			ro.snapshot().close();
		}
	}

	
	/**
	 * insertamos secuencial, no es lo habitual
	 * recuperamos random, es lo habitual
	 * 
	 * Asombrosamente en esta prueba con 5MM de put y luego get, el Java port es ligeramente más rápido que el native JNI !!
	 * Como comparativa, Cache2k demoró 18s con 5MM de capacidad de entradas y 45 con 2MM
	 * */
	private void dummiesPerformanceTest() throws IOException {
		long init = System.currentTimeMillis();
		long checkpoint = init;
		for (int i = 0; i < 5000000; i++) {
			db.put(bytes("key-"+i), bytes("abcdefghijklmnopqrstuvwxyz"));
			
			/* DELETING THE DDBB DIDNT WORK!
			if (System.currentTimeMillis()-checkpoint > 1000*20) {//cada 20s chequea
				checkpoint = System.currentTimeMillis();
				long[] sizes = db.getApproximateSizes(new Range(bytes("a"), bytes("z")));
				if (sizes[0] > 50000000) {//si es mayor a 50MB borramos todo
					Options options = new Options();
					factory.destroy(new File(pathToStore), options);
					openingAndClosingTheDatabase();
				}
			}*/
		}
		for (int i = 0; i < 5000000; i++) {
			db.get(bytes("key-"+ThreadLocalRandom.current().nextInt(0, 5000000 + 1)));
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis()-init));
	}

}
