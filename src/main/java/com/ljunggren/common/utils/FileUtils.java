package com.ljunggren.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

public class FileUtils {

	public static String readFileFromResources(String path) throws IOException {
		InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(path);
		return IOUtils.toString(inputStream);
	}

	public static String readFile(String absolutePath) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(absolutePath));
		String output = lines.collect(Collectors.joining(System.lineSeparator()));
		lines.close();
		return output;
	}

	public static void truncateFile(String absolutePath) throws IOException {
		Files.write(Paths.get(absolutePath), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	public static void appendToFile(String absolutePath, String string) throws IOException {
		Files.write(Paths.get(absolutePath), string.getBytes(), StandardOpenOption.APPEND);
	}
	
}
