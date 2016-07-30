package com.zombymatthew.flipper;

import java.nio.file.Path;

public class App 
{
  protected Path watchPath = null;
  public static void main (String [] args) throws Exception
  {


    Flipper flip = new Flipper (null);
  }
 
  
  public void parseArguments (String [] args)
  {
    for (String argument: args)
    {
      if (argument.startsWith ("-"))
      {
        if (argument.contains ("="))
        {
          
        }
        else
        {
          
        }

      }
      else 
      {
        System.err.println ("Unknow argument: " + argument);
      }
    }
    
  }
  
  

}
