package com.zombymatthew.flipper.importer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import com.zombymatthew.flipper.CONFIG;
import com.zombymatthew.flipper.FlipperLog;

public class ImporterFactory
{
  public static FlipperImporter getImporter (Properties config, Path rootPath, FlipperLog log) throws Exception
  {
    String type = config.getProperty (CONFIG.TYPE);
    String destination = config.getProperty (CONFIG.DESTINATION);
    Path destinationPath = Paths.get (destination);
    File fileDestination = destinationPath.toFile ();
    if (!fileDestination.exists ())
      log.error ("File does not exist");

    if (type.equals (CONFIG.PICTURE_TYPE))
    {
      String movieDest = config.getProperty (CONFIG.PICTURES.MOVIE_DESTINATION);
      if (movieDest == null || movieDest.length () == 0)
      {
        log.error ("Movie destination path not set"); 
        return null;
      }
      Path movieDestPath = Paths.get (movieDest);
      PictureImporter pictImport = new PictureImporter (rootPath, log, destinationPath, movieDestPath);
      
      String picturePathTemplate = config.getProperty (CONFIG.PICTURES.PICTURE_PATH_TEMPLATE);
      if (picturePathTemplate != null && picturePathTemplate.length () > 0)
        pictImport.setPicturePathTemplate (picturePathTemplate);
      
      String moviePathTemplate = config.getProperty (CONFIG.PICTURES.MOVIE_PATH_TEMPLATE);
      if (moviePathTemplate != null && moviePathTemplate.length () > 0)
        pictImport.setMoviePathTemplate (moviePathTemplate);
      
      
      return pictImport;
    }
    else
    {
      return null;
    }
  }
  

}
