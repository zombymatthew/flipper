package com.zombymatthew.flipper;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardOpenOption.APPEND;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlipperLog
{
  private Path logFilePath;
  private boolean toFile = true;
  private boolean timestamp = true;
  private boolean debug = true;
  //private boolean verbose = false;

  public FlipperLog (Path rootPath) throws Exception
  {
    super ();
    SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMdd-HHmmss");
    String logFileName = sdf.format (new Date ()) + ".log";
    Path logFolder = rootPath.resolve ("logs");
    File logFolderFile = logFolder.toFile ();
    if (!logFolderFile.exists ())
      logFolderFile.mkdir ();
    logFilePath = logFolder.resolve (logFileName);
    File logFile = logFilePath.toFile (); 
    if (logFile.exists ())
      System.err.println ("Error: " + logFile.getAbsolutePath () + " file already exists." );
    else
      logFile.createNewFile ();
  }

  public void error (String message) throws Exception
  {
    String mess = addTimestamp (message);
    logToFile (mess);
    System.err.println (mess);
  }

  public void error (Exception ex, String message) throws Exception
  {
    String mess = addTimestamp (message + ": " + ex.getMessage ());
    logToFile (mess);
    System.err.println ();
  }

  public void error (Exception ex) throws Exception
  {
    String mess = addTimestamp ("Error: " + ex.getMessage ());
    logToFile (mess);
    System.err.println (mess);
  }
  
  public void debug (String message) throws Exception
  {
    if (debug)
    {
      String mess = addTimestamp (message);
      logToFile (mess);
      System.out.println (message); 
    }
  }
  
  public void console (String message) throws Exception
  {
    String mess = addTimestamp (message);
    logToFile (mess);
    System.out.println (message);
  }
  
  private String addTimestamp (String message)
  {
    if (timestamp)
      return new Date().toString () + " - " + message;
    else
      return message;
  }

  private void logToFile (String message) throws Exception
  {
    if (toFile)
    {
      byte [] b = (message + "\n").getBytes ();
      Files.write (logFilePath, b, CREATE, WRITE, APPEND); 
    }
  }
}
