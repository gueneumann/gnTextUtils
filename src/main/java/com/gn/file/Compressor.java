package com.gn.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
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
}
