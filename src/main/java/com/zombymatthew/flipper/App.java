package com.zombymatthew.flipper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.XMLConfiguration;

public class App 
{
  protected Path watchPath = null;
  protected XMLConfiguration xmlConfig = null;

  public static void main (String [] args) throws Exception
  {
    parseArguments (args);
    


    Flipper flip = new Flipper (null);
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
      System.out.println(e.getMessage());
      formatter.printHelp("flipper", options);

      System.exit(1);
      return;
    }

    String directory = cmd.getOptionValue ("p");
    
  }
  
  

}
