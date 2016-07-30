package com.zombymatthew.flipper;

import java.nio.file.Path;
import java.util.Properties;

import com.zombymatthew.flipper.importer.FlipperImporter;
import com.zombymatthew.flipper.importer.ImporterFactory;

public class Flipper
{
  private Properties config;
  private Path pathToScan;
  private FlipperImporter importer;
  private FlipperLog log;

  public Flipper (Path pathToScan, Properties config, FlipperLog log)
  {
    super ();
    this.config = config;
    this.pathToScan = pathToScan;
    this.log = log;
  }
  
  public void run ()
  {
     importer = ImporterFactory.getImporter (config, pathToScan, log); 
     log.debug ("testing");

     importer.doImport ();
    
  }
}
