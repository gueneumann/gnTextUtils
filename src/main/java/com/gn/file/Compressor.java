package com.gn.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class Compressor {

	// From http://stackoverflow.com/questions/4834721/java-read-bz2-file-and-uncompress-parse-on-the-fly
	// Reads in a compressed file
	// Note: the accepted formats are: gzip, bzip2, xz, lzma, Pack200, DEFLATE and Z. 
	// As seen in the link, the correct one is automatically assigned – Danielson Aug 15 at 10:01 

	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) 
			throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
		return br2;
	}

	public static BufferedReader getBufferedReaderForUncompressedFile(String fileName, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException {
		FileInputStream fin = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader (new InputStreamReader (fin, encoding));
		return br;
	}

	public static BufferedWriter getBufferedWriterForCompressedTextFile(String fileOut) 
			throws FileNotFoundException, CompressorException, UnsupportedEncodingException {
		FileOutputStream fout = new FileOutputStream(fileOut);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		CompressorOutputStream out = new CompressorStreamFactory().createCompressorOutputStream("bzip2", bout);
		BufferedWriter br2 = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
		return br2;
	}

	public static BufferedWriter getBufferedWriterForUncompressedTextFile(String fileOut, String encoding) 
			throws FileNotFoundException, UnsupportedEncodingException {
		FileOutputStream fout = new FileOutputStream(fileOut);
		BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (fout, encoding));
		return bw;			
	}
}
