package com.zombymatthew.flipper;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args ) throws Exception
  {
    WatchService watcher = FileSystems.getDefault().newWatchService();
  
    Path dir = FileSystems.getDefault ().getPath("c:\\Transfer", "test");

    try 
    {
      dir.register(watcher,
                          ENTRY_CREATE,
                          ENTRY_DELETE,
                          ENTRY_MODIFY);
    }
    catch (IOException x) 
    {
      System.err.println(x);
   	}
    
    while (true) 
    {
      WatchKey key;
      try 
      {
        key = watcher.take();
      } 
      catch (InterruptedException x) 
      {
        return;
      }
      
      processWatchEvents (key);
      
      // IMPORTANT: The key must be reset after processed
      boolean valid = key.reset();
      if (!valid) 
        break;
    }
  }
  
  private static void processWatchEvents (WatchKey key)
  {
    for (WatchEvent<?> event : key.pollEvents()) 
    {
      
      WatchEvent.Kind<?> kind = event.kind();
 
      System.out.println(kind.name() + ": " + getFileName (event));
 
      if (kind == OVERFLOW) 
      {
        continue;
      } 
      else if (kind == ENTRY_CREATE) 
      {
        
            // process create event
      } 
      else if (kind == ENTRY_DELETE) 
      {
            // process delete event
      } 
      else if (kind == ENTRY_MODIFY) 
      {
            // process modify event
      }
    }
  }
  
  private static String getFileName (WatchEvent event)
  {
    @SuppressWarnings("unchecked")
    WatchEvent<Path> ev = (WatchEvent<Path>) event;
    Path fileName = ev.context();
    return fileName.toString ();
  }
}
