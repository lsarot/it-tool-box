package com.example.leo.infinispandemo.util;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.*;
import org.infinispan.notifications.cachelistener.event.*;

@Listener
public class CacheListener {
    
	/** entry created
	 * */
	@CacheEntryCreated
    public void entryCreated(CacheEntryCreatedEvent<String, String> event) {
        this.printLog("Adding key '" + event.getKey() + "' to cache", event);
    }
 
	/** entry expired
	 * */
    @CacheEntryExpired
    public void entryExpired(CacheEntryExpiredEvent<String, String> event) {
        this.printLog("Expiring key '" + event.getKey() + "' from cache", event);
    }
 
    /** entry retrieved
     * */
    @CacheEntryVisited
    public void entryVisited(CacheEntryVisitedEvent<String, String> event) {
        this.printLog("Key '" + event.getKey() + "' was visited", event);
    }
 
    /** restored from disk to cache
     * the entry is now accessible in Infinispan again (occurs after @EntryLoaded)
     * */
    @CacheEntryActivated
    public void entryActivated(CacheEntryActivatedEvent<String, String> event) {
        this.printLog("Activating key '" + event.getKey() + "' on cache", event);
    }
 
    /** saved to disk from cache
     * */
    @CacheEntryPassivated
    public void entryPassivated(CacheEntryPassivatedEvent<String, String> event) {
        this.printLog("Passivating key '" + event.getKey() + "' from cache", event);
    }
 
    /** when trying to reach our passivated entry, Infinispan checks it's stored contents and load the entry to the memory again
     * */
    @CacheEntryLoaded
    public void entryLoaded(CacheEntryLoadedEvent<String, String> event) {
        this.printLog("Loading key '" + event.getKey() + "' to cache", event);
    }
 
    /** evicted from cache
     * */
    @CacheEntriesEvicted
    public void entriesEvicted(CacheEntriesEvictedEvent<String, String> event) {
        StringBuilder builder = new StringBuilder();
        event.getEntries().forEach(
          (key, value) -> builder.append(key).append(", "));
        System.out.println("Evicting following entries from cache: " + builder.toString());
    }
 
    /** Before printing our message we check if the event being notified already has happened, because, for some event types, Infinispan sends two notifications: one before and one right after it has been processed.
     * */
    private void printLog(String log, CacheEntryEvent event) {
        if (!event.isPre()) {
            System.out.println(log);
        }
    }
}
