package com.zombymatthew.flipper.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import com.zombymatthew.flipper.FlipperLog;

public class PictureImporter extends Importer implements FlipperImporter
{
  private Path destPath;
  private int success = 0;
  private int failed = 0;
  public PictureImporter (Path rootPath, FlipperLog log, Path destPath)
  {
    super (rootPath, log);
    this.destPath = destPath;
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
    log.debug ("Success: " + success);
    log.debug ("Failed: " + failed);
  }
  
 
  @Override
  protected void processFile (File file)
  {
    String fileExt = getFileExtension (file);
    if (fileExt.equalsIgnoreCase ("JPG"))
    {
      Date dateTaken = getDateTaken (file);
      
      if (dateTaken == null)
      {
        log.error ("File - " + file.getAbsolutePath () + " - does not have a date taken attribute.");
        failed++;
        return;
      }
        
      String newFilePath = getNewFilePath (file, dateTaken);
      Path newPath = destPath.resolve (newFilePath);
      newPath.toFile ().mkdirs ();
      newPath = newPath.resolve (file.getName ());
      Path origPath = Paths.get (file.toURI ());

      try
      {
        copyFile (origPath, newPath);
        verifyFile (origPath, newPath);
        deleteOriginalFile (origPath);
        success++;
      }
      catch (Exception ex)
      {
        log.error (ex);
        failed++;
        return;
      }
      log.debug (newPath.toString ());
    }
    else
    {
      log.debug ("Unknown file type: " + file.getAbsolutePath ());
      failed++;
    }
  }
  
  private boolean copyFile (Path origPath, Path newPath) throws IOException
  {
    log.debug ("Copying file " + origPath.toString () + " to " + newPath.toString ());
    Files.copy (origPath, newPath, COPY_ATTRIBUTES);
    return true;
  }
  
  private boolean verifyFile (Path origPath, Path newPath)
  {

    return true;
  }

  private void deleteOriginalFile (Path origPath) throws Exception
  {
    Files.delete (origPath);
 //TODO: delete any empty folders?
  }

  private Date getDateTaken (File file)
  {
    try
    {
      Date dateTaken = null;
      Metadata metadata = ImageMetadataReader.readMetadata(file);
      for (ExifSubIFDDirectory subIFD: metadata.getDirectoriesOfType(ExifSubIFDDirectory.class))
      {
        dateTaken = subIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
      }

      if (dateTaken == null)
      {
        for (FileMetadataDirectory fileMetaDir: metadata.getDirectoriesOfType (FileMetadataDirectory.class))
        {
          dateTaken = fileMetaDir.getDate (FileMetadataDirectory.TAG_FILE_MODIFIED_DATE); 
        }
      }
      //TODO: Maybe get the date from somewhere else???

      if (dateTaken == null)
      {
        spewMetadata (metadata);
      }
      return dateTaken;
    }
    catch (Exception ex)
    {
      log.error (ex);
      return null;
    }
  }

  private void spewMetadata (Metadata metadata)
  {
    for (Directory directory : metadata.getDirectories()) 
    {
      for (Tag tag : directory.getTags()) 
      {
        log.console (tag.toString ());
      }
    }
  }

  private String getNewFilePath (File file, Date dateTaken)
  {
    SimpleDateFormat sdfYear = new SimpleDateFormat ("yyyy");
    SimpleDateFormat sdfMonth = new SimpleDateFormat ("MMMM");
    SimpleDateFormat sdfDay = new SimpleDateFormat ("dd");
    StringBuilder sb = new StringBuilder ();
    sb.append (sdfYear.format (dateTaken));
    sb.append (File.separator);
    sb.append (sdfMonth.format (dateTaken));
    sb.append (File.separator);
    sb.append (sdfDay.format (dateTaken));
    //sb.append (File.separator);
    //sb.append (file.getName ());
    return sb.toString ();
  }
}
