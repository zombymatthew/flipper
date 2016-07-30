package com.zombymatthew.flipper;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App 
{
  protected static Path watchPath = null;

  public static void main (String [] args) throws Exception
  {
    parseArguments (args);
    FlipperLog log = new FlipperLog (watchPath);
    
    File configFile = watchPath.resolve ("config.properties").toFile ();
    if (!configFile.exists ())
      System.out.println ("Error: " + configFile.getAbsolutePath () + " does not exist.");
    else
    {
      Properties props = getConfiguration (configFile);
      Flipper flip = new Flipper (watchPath, props, log);
      flip.run ();
    }
  }
 
  private static Properties getConfiguration (File propertiesFile)
  {
    try
    {
      FileInputStream fis = new FileInputStream (propertiesFile);
      Properties props = new Properties();
      props.load (fis);
      return props;
    }

    catch(Exception ex)
    {
      System.err.println ("Loading of file " + propertiesFile + " has failed: " + ex.getMessage ());
      System.exit (1);
      return null;
    }
  }
  
  private static void parseArguments (String [] args)
  {
    Options options = new Options ();
    
    Option optPathToScan = new Option ("p", "pathToScan", true, "The path to the directory to scan");
    optPathToScan.setRequired (true);
    options.addOption (optPathToScan);
    
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;
    
    try 
    {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) 
    {
      System.err.println (e.getMessage ());
      formatter.printHelp("flipper", options);

      System.exit(1);
      return;
    }

    String directory = cmd.getOptionValue ("p");
    Path p = Paths.get (directory);
    File f = p.toFile ();
    if (!f.exists () || !f.isDirectory ())
    {
      System.err.println ("Error: " + f.getAbsolutePath () + " either does not exist or is not a directory.");
      System.exit (1);
      return;
    }
    else 
    {
      watchPath = p;
      return;
    }
  }
  
  

}
