package com.zombymatthew.flipper.importer;

import java.nio.file.Path;
import java.util.Properties;

import com.zombymatthew.flipper.CONFIG;
import com.zombymatthew.flipper.FlipperLog;

public class ImporterFactory
{
  public static FlipperImporter getImporter (Properties config, Path rootPath, FlipperLog log)
  {
    String type = config.getProperty (CONFIG.TYPE);
    if (type.equals (CONFIG.PICTURE_TYPE))
    {
      PictureImporter pictImport = new PictureImporter (rootPath, log);
      
      
      return pictImport;
    }
    else
    {
      return null;
    }
  }
  

}
