package com.zombymatthew.flipper.importer;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import com.zombymatthew.flipper.CONFIG;
import com.zombymatthew.flipper.FlipperLog;

public class PictureImporter extends Importer implements FlipperImporter
{
  private Path destPath;
  private Path movieDestPath;

  private String pictPathTemplate = CONFIG.PICTURES.DEFAULT_PICTURE_PATH_TEMPLATE;
  private String moviePathTemplate = CONFIG.PICTURES.DEFAULT_MOVIE_PATH_TEMPLATE;

  private int success = 0;
  private int failed = 0;

  public PictureImporter (Path rootPath, FlipperLog log, Path destPath, Path moviePath)
  {
    super (rootPath, log);
    this.destPath = destPath;
    this.movieDestPath = moviePath;
  }
  
  public void setPicturePathTemplate (String template)
  {
    this.pictPathTemplate = template;
  }
  
  public void setMoviePathTemplate (String template)
  {
    this.moviePathTemplate = template;
  }

  public void doImport () throws Exception
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
  protected void processFile (File file) throws Exception
  {
    if (isPicture (file))
    {
      doProcessing (file, destPath, pictPathTemplate);
    }
    else if (isMovie (file))
    {
      doProcessing (file, movieDestPath, moviePathTemplate);
    }
    else
    {
      log.debug ("Unknown file type: " + file.getAbsolutePath ());
      failed++;
    }
  }
  
  private void doProcessing (File file, Path destination, String filePathFormat) throws Exception
  {
    Date dateTaken = getDateTaken (file);

    if (dateTaken == null)
    {
      log.error ("File - " + file.getAbsolutePath () + " - does not have a date taken attribute.");
      failed++;
      return;
    }

    String newFilePath = getNewFilePath (dateTaken, filePathFormat);
    Path newPath = destination.resolve (newFilePath);
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
    // TODO Auto-generated method stub

  }

  private boolean copyFile (Path origPath, Path newPath) throws Exception
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

  private Date getDateTaken (File file) throws Exception
  {
    Date dateTaken = null;

    dateTaken = getDateTakenViaTika (file);

    if (dateTaken == null)
    {
      dateTaken = getDateTakenViaDrew (file);
    }
//TODO: maybe add a few more methods of getting the date taken
    return dateTaken;

  }

  private Date getDateTakenViaTika (File file) throws Exception
  {
    try
    {
      AutoDetectParser parser = new AutoDetectParser();
      BodyContentHandler handler = new BodyContentHandler();
      org.apache.tika.metadata.Metadata fileMetadata = new org.apache.tika.metadata.Metadata();   //empty metadata object 
      FileInputStream inputstream = new FileInputStream(file);
      ParseContext context = new ParseContext();
      parser.parse(inputstream, handler, fileMetadata, context);
      return fileMetadata.getDate (TikaCoreProperties.CREATED);
    }
    catch (Exception ex)
    {
      log.error ("Unable to parse date taken with Tika");
      return null;
    }
  }
  
  private Date getDateTakenViaDrew (File file) throws Exception
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
      log.error ("Unable to parse date taken via Drew: " + ex.toString ());
      return null;
    }
  }
  
  private boolean isPicture (File file)
  {
    String fileExt = getFileExtension (file);
    if (fileExt.equalsIgnoreCase ("JPG"))
      return true;
    else 
      return false;
  }
  
  private boolean isMovie (File file)
  {
    String fileExt = getFileExtension (file);
    if (fileExt.equalsIgnoreCase ("MP4") ||
        fileExt.equalsIgnoreCase ("MTS") ||
        fileExt.equalsIgnoreCase ("WMV") ||
        fileExt.equalsIgnoreCase ("MPG") ||
        fileExt.equalsIgnoreCase ("M2T"))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  private void spewMetadata (Metadata metadata) throws Exception
  {
    for (Directory directory : metadata.getDirectories()) 
    {
      for (Tag tag : directory.getTags()) 
      {
        log.console (tag.toString ());
      }
    }
  }

  private String getNewFilePath (Date dateTaken, String template)
  {
    String [] template_pieces = parseTemplate (template);
    StringBuilder filePathBuilder = new StringBuilder ();
    boolean first = true;
    for (String piece: template_pieces)
    {
      SimpleDateFormat sdf = new SimpleDateFormat (piece);
      if (first)
        first = false;
      else
        filePathBuilder.append (File.separator);
      filePathBuilder.append (sdf.format (dateTaken));
    }
    return filePathBuilder.toString ();
  }

  private String [] parseTemplate (String template)
  {
    String [] splits = template.split ("/");
    String [] pieces = new String [splits.length];
    for (int i = 0; i < splits.length; i++)
    {
      String split = splits [i];
      if (split.startsWith ("{") && split.endsWith ("}"))
      {
        String value = split.substring (1, split.length () -1); 
        pieces [i] = value;
      }
    }

    return pieces;
  }
}
