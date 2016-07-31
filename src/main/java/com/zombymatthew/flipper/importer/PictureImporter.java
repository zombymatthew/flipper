package com.zombymatthew.flipper.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.zombymatthew.flipper.FlipperLog;

public class PictureImporter extends Importer implements FlipperImporter
{
  public PictureImporter (Path rootPath, FlipperLog log)
  {
    super (rootPath, log);
  }

  public void doImport ()
  {
    File root = rootPath.toFile ();

    final FilenameFilter filter = (dir, name) -> {
      if (name.equals ("logs") || name.equalsIgnoreCase ("config.properties") || name.startsWith ("."))
        return false;
      else 
        return true;
    };
    processFiles (root.listFiles (filter));
  }
  
 
  @Override
  protected void processFile (File file)
  {
    String fileExt = getFileExtension (file);
    if (fileExt.equalsIgnoreCase ("JPG"))
    {
      try
      {
        Metadata metadata = ImageMetadataReader.readMetadata(file);

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
      }
      catch (Exception ex)
      {
        log.error (ex);
      }
 
    }
    else
    {
      log.debug ("Unknown file type: " + file.getAbsolutePath ());
    }
  }
}
