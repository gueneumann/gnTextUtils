package com.gn.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorException;

// This is a test that I need to parse compressed files from stackoverflow at 
// /Volumes/data2/StackExchange/stackexchange

public class ReadSevenZFile {

	private int counter = 0;

	private int upper = 10;

	// Does not make sense, 7z just returns a single entry
	public void testReader(String fileName) throws IOException {

		SevenZFile sevenZFile = new SevenZFile(new File(fileName));
		SevenZArchiveEntry entry = sevenZFile.getNextEntry();

		while(entry!=null){
			System.err.println(counter);
			if (this.counter == this.upper){
				break;
			}
			else{
				System.out.println(entry.getName() + " " + entry.getSize());
				FileOutputStream out = new FileOutputStream(entry.getName());

				byte[] content = new byte[(int) entry.getSize()];
				sevenZFile.read(content, 0, content.length);
				out.write(content);
				out.close();
				entry = sevenZFile.getNextEntry();
				counter++;
			}
		}
		sevenZFile.close();
	}

	public static void main(String[] args) throws IOException, CompressorException {
		ReadSevenZFile reader = new ReadSevenZFile();

		String fileName = "/Volumes/data2/StackExchange/stackexchange/stackoverflow.com-Posts.7z";
		reader.testReader(fileName);
	}
}
