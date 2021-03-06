package com.zombymatthew.flipper.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;

import com.zombymatthew.flipper.FlipperLog;

public abstract class Importer
{
  protected Path rootPath;
  protected FlipperLog log;
  
  public Importer (Path path, FlipperLog log)
  {
    this.rootPath = path;
    this.log = log;
  }
  
  protected abstract void processFile (File file) throws Exception;
  
  protected void processDirectory (File directory) throws Exception
  {
    if (!directory.isDirectory ())
      log.error ("File found where a directory should be.");
    else
    {
      final FilenameFilter filter = (dir, name) -> {
        if (name.equals ("logs") || name.equalsIgnoreCase ("config.properties") || name.startsWith ("."))
          return false;
        else 
          return true;
      };
      processFiles (directory.listFiles (filter));

    }
  }
  
  protected void processFiles (File [] files) throws Exception
  {
    for (File file: files)
    {
      if (file.isDirectory ())
        processDirectory (file);
      else
        processFile (file);
    }
  }
  
  protected String getFileExtension (File file)
  {
    String fileName = file.getName ();
    int lastPeriod = fileName.lastIndexOf (".");
    return fileName.substring (lastPeriod + 1);
  }

}
