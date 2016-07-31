package com.zombymatthew.flipper.importer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.zombymatthew.flipper.CONFIG;
import com.zombymatthew.flipper.FlipperLog;

public class ImporterFactory
{
  public static FlipperImporter getImporter (Properties config, Path rootPath, FlipperLog log)
  {
    String type = config.getProperty (CONFIG.TYPE);
    String destination = config.getProperty (CONFIG.DESTINATION);
    Path destinationPath = Paths.get (destination);
    File fileDestination = destinationPath.toFile ();
    if (!fileDestination.exists ())
      log.error ("File does not exist");

    if (type.equals (CONFIG.PICTURE_TYPE))
    {
      PictureImporter pictImport = new PictureImporter (rootPath, log, destinationPath);
      
      
      return pictImport;
    }
    else
    {
      return null;
    }
  }
  

}
