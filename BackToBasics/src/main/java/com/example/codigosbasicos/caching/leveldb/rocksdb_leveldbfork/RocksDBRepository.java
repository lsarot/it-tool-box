package com.example.codigosbasicos.caching.leveldb.rocksdb_leveldbfork;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Optional;

import javax.annotation.PostConstruct;

public class RocksDBRepository implements KVRepository<String, Object> {

	RocksDB db;
	
	public RocksDBRepository(RocksDB db) {
		this.db = db;
	}

	
	@Override
	public synchronized boolean save(String key, Object value) {
		// log.info("saving value '{}' with key '{}'", value, key);
		try {
			db.put(key.getBytes(), convertToBytes(value));
		} catch(	RocksDBException | IOException e)	{
	      //log.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());
	      return false;
	    }
		return true;
	}

	
	@Override public synchronized Optional<Object> find(String key) {
	    Object value = null;
	    try {
	    	byte[] bytes = db.get(key.getBytes());
	    	if (bytes != null) 
	    		value = convertFromBytes(bytes);
	    } catch (RocksDBException | ClassNotFoundException | IOException e) {
	      /*log.error(
	        "Error retrieving the entry with key: {}, cause: {}, message: {}", 
	        key, 
	        e.getCause(), 
	        e.getMessage()
	      );*/
	    }
	    //log.info("finding key '{}' returns '{}'", key, value);
	    return value != null ? Optional.of(value) : Optional.empty();
	  }

	
	@Override public synchronized boolean delete(String key) {
	    //log.info("deleting key '{}'", key);
		try {
			db.delete(key.getBytes());
	    } catch (RocksDBException e) {
	    	//log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
	    	return false;
	    }
		return true;
	  }

	
		private byte[] convertToBytes(Object object) throws IOException {
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos)) {
				out.writeObject(object);
				return bos.toByteArray();
			}
		}

		private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
			try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
					ObjectInput in = new ObjectInputStream(bis)) {
				return in.readObject();
			}
		}

}
