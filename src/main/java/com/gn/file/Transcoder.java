package com.gn.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * {@link Transcoder} provides a method to convert a file from one
 * encoding to another.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public final class Transcoder {

  /**
   * Would create a new instance of {@link Transcoder}. Not to be used.
   */
  private Transcoder() {

    // private constructor to enforce noninstantiability
  }


  /**
   * Converts the given source file with the given source encoding into the
   * given target file with the target encoding.
   *
   * @param sourceFileName
   *          the source file name
   * @param sourceEncoding
   *          the source encoding
   * @param targetFileName
   *          the target file name
   * @param targetEncoding
   *          the target encoding
   * @throws IOException
   *           if there is a problem when reading or writing the files or if
   *           either of the given encoding names can't be matched to a Java
   *           Charset
   */
  public static void transcode(String sourceFileName, String sourceEncoding,
      String targetFileName, String targetEncoding)
      throws IOException {

    // init reader
    BufferedReader reader = new BufferedReader(
      new InputStreamReader(
        new FileInputStream(sourceFileName),
        sourceEncoding));

    // init writer
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(
        new FileOutputStream(targetFileName),
        targetEncoding));

    // write each character from the source file to the target file; this is
    // done character by character instead of line by line to exactly copy the
    // source's line termination character(s) which wouldn't be possible in some
    // cases otherwise (java.io.BufferedWriter.newLine() uses a system dependent
    // line separator instead of the one from the source file).
    int currChar = 0;
    while ((currChar = reader.read()) != -1) {
      writer.write(currChar);
    }

    reader.close();
    writer.close();
  }
}
