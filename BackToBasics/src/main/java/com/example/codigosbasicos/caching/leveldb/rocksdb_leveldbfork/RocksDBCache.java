package com.example.codigosbasicos.caching.leveldb.rocksdb_leveldbfork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rocksdb.BlockBasedTableConfig;
import org.rocksdb.CompressionType;
import org.rocksdb.LRUCache;
import org.rocksdb.Options;
import org.rocksdb.PlainTableConfig;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * RocksDB is a LevelDB fork made by Facebook
 * 
 * Parece más elaborada la API, pero incluso con configuración demora el doble!
 */
public class RocksDBCache {

	//private static Logger log = LogManager.getLogger(RocksDBCache.class);

	private final static String FILE_NAME = "rocksdb-store";
	File baseDir;
	RocksDB db;
	RocksDBRepository repo;

	public static void main(String[] args) {
		new RocksDBCache().run();
	}

	private void run() {
		try {
			openingAndClosingTheDatabase();

			dummiesPerformanceTest();

		} catch (IOException | RocksDBException e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	
	/** There are a couple of components in RocksDB that contribute to memory usage:
		    Block cache
		    Indexes and bloom filters
		    Memtables
		    Blocks pinned by iterators
		    
		*  Cómo optimizar memoria:
		*  https://github.com/facebook/rocksdb/wiki/Setup-Options-and-Basic-Tuning
		*  https://zhuanlan.zhihu.com/p/63156205
	 * */
	private void openingAndClosingTheDatabase() throws RocksDBException {
		RocksDB.loadLibrary();
		
		BlockBasedTableConfig tableConf = new BlockBasedTableConfig() //PlainTableConfig
				.setBlockCache(new LRUCache(100 * 1024 * 1024, -1, false))
				.setBlockCacheCompressed(new LRUCache(100 * 1024 * 1024, -1, false)) //ClockCache
				.setBlockSize(16 * 1024) //16,32... increasing it will reduce index and filters size
				//.setPersistentCache(persistentCache)
				//.setEnableIndexCompression(true)
						/* There are two options that configure how much index and filter blocks we fit in memory:
							1. If you set cache_index_and_filter_blocks to true, index and filter blocks will be stored in block cache, together with all other data blocks. This also means they can be paged out. If your access pattern is very local (i.e. you have some very cold key ranges), this setting might make sense. However, in most cases it will hurt your performance, since you need to have index and filter to access a certain file. Always consider to set pin_l0_filter_and_index_blocks_in_cache too to minimize the performance impact.
							2. If cache_index_and_filter_blocks is false (which is default), the number of index/filter blocks is controlled by option max_open_files. If you are certain that your ulimit will always be bigger than number of files in the database, we recommend setting max_open_files to -1, which means infinity. This option will preload all filter and index blocks and will not need to maintain LRU of files. Setting max_open_files to -1 will get you the best possible performance. */
				//.setCacheIndexAndFilterBlocks(true)
						//.setPinL0FilterAndIndexBlocksInCache(true) //mejora performance si activamos .setCacheIndexAndFilterBlocks(true)
				;

		
		// memory being used by index and filter blocks
		// db.getProperty("rocksdb.estimate-table-readers-mem");
		// current memtable size
		// "rocksdb.cur-size-all-mem-tables"
		
		final Options options = new Options()
				.setTableFormatConfig(tableConf)
				//.setCompressionType(CompressionType.LZ4_COMPRESSION)
				.setOptimizeFiltersForHits(true) // If you're certain that Get() will mostly find a key you're looking for, you can set options.optimize_filters_for_hits = true. With this option turned on, we will not build bloom filters on the last level, which contains 90% of the database. Thus, the memory usage for bloom filters will be 10X less. You will pay one IO for each Get() that doesn't find data in the database, though.
				.setMaxOpenFiles(-1)
				/* You can think of memtables as in-memory write buffers. Each new key-value pair is first written to the memtable. Memtable size is controlled by the option write_buffer_size. It's usually not a big memory consumer. However, memtable size is inversely proportional to write amplification -- the more memory you give to the memtable, the less the write amplification is. If you increase your memtable size, be sure to also increase your L1 size! L1 size is controlled by the option max_bytes_for_level_base.	*/
				//.setDbWriteBufferSize(0)
				.setWriteBufferSize(100 * 1024 * 1024) //64 default
					.setMaxBytesForLevelBase(360 * 1024 * 1024) //256 default
					//.setMaxBytesForLevelMultiplier(multiplier)
				;
				//options.setMaxOpenFiles(maxOpenFiles)
				//options.setOptimizeFiltersForHits(optimizeFiltersForHits)
				//options.setMaxFileOpeningThreads(maxFileOpeningThreads)
				options.setCreateIfMissing(true);

				
		baseDir = new File(FILE_NAME);
		try {
			// Files.createDirectories(baseDir.getParentFile().toPath());
			Files.createDirectories(baseDir.getAbsoluteFile().toPath());
			db = RocksDB.open(options, baseDir.getAbsolutePath());
			//log.info("RocksDB initialized");
		} catch (IOException | RocksDBException e) {
			//log.error("Error initializing RocksDB. Exception: '{}', message: '{}'",  e.getCause(), e.getMessage(), e);
		}

		repo = new RocksDBRepository(db);
	}

	
	private void dummiesPerformanceTest() throws IOException {
		long init = System.currentTimeMillis();
		long checkpoint = init;
		for (int i = 0; i < 5000000; i++) {
			repo.save("key-" + i, "abcdefghijklmnopqrstuvwxyz");
		}
		for (int i = 0; i < 5000000; i++) {
			repo.find("key-" + ThreadLocalRandom.current().nextInt(0, 5000000 + 1));
		}
		System.out.println("Dummy test took ms : " + (System.currentTimeMillis() - init));
	}

}
