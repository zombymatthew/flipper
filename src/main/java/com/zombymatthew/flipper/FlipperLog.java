package com.zombymatthew.flipper;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlipperLog
{
  private File logFile;
  private boolean toFile = false;
  private boolean timestamp = false;
  private boolean debug = true;
  private boolean verbose = false;

  public FlipperLog (Path rootPath) throws Exception
  {
    super ();
    SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMdd-HHmmss");
    String logFileName = sdf.format (new Date ()) + ".log";
    Path logFolder = rootPath.resolve ("logs");
    File logFolderFile = logFolder.toFile ();
    if (!logFolderFile.exists ())
      logFolderFile.mkdir ();
    File logFile = logFolder.resolve (logFileName).toFile (); 
    if (logFile.exists ())
      System.err.println ("Error: " + logFile.getAbsolutePath () + " file already exists." );
    else
    {
      logFile.createNewFile ();
      this.logFile = logFile;
    }
  }

  public void error (String message)
  {
    System.err.println (message);
  }

  public void error (Exception ex)
  {
    System.err.println ("Error: " + ex.getMessage ());
  }
  
  public void debug (String message)
  {
    if (debug)
      System.out.println (message); 
  }
  
  public void console (String message)
  {
    System.out.println (message);
  }
}
