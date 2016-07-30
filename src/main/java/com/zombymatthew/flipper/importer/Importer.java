package com.zombymatthew.flipper.importer;

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

}
