package vn.edu.hcmus.dhhai.android.graphextractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;


public class FileUtils {

	/**
	 *  Returns a text file content as an array of strings,
	 *  where each string is a line.
	 *  Compatible with Java 6.
	 *  @param  path absolute path to a text file
	 *  @return      lines from the text file
	 */
	public static String[] getAllLines(String path) {
		FileReader fin;
		BufferedReader reader;
		List<String> lines = new ArrayList<String>();
		try {
			fin = new FileReader(path);
			reader = new BufferedReader(fin);

			String line;
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}

		} catch (Exception ex) {
			// silent
		}
		finally {
			return lines.toArray(new String[0]);
		}
	}

	/**
	 *  List the absolute paths of interesting files from a given folder
	 *  Example usage:
	 *	FileUtils.getJavaFiles("/Users/hans/Desktop/ast/astparser", 
	 *				new String[] {".java", ".JAVA"});
	 *  @param	path 	absolute path to the folder
	 *  @param  extensions array of interesting file extensions to list
	 *  @return 		return the absolute paths to interesting files if found
	 */
	public static String[] getFiles(String path, String[] extensions) {
		return FileUtils.getFiles(path, extensions, true);
	}

	/**
	 *  List the paths of interesting files from a given folder
	 *  Example usage:
	 *	FileUtils.getJavaFiles("/Users/hans/Desktop/ast/astparser", 
	 *				new String[] {".java", ".JAVA"});
	 *  @param	path 	absolute path to the folder
	 *  @param  extensions array of interesting file extensions to list
	 *  @param  absolute if true, will return absolute paths, if false, only
	 *						file names are returned
	 *  @return 		return the paths to interesting file sif found
	 */
	public static String[] getFiles(String path, String[] extensions,
						 boolean absolute) {
		File folder = new File(path);
		if (!folder.isDirectory())
			return null;

		if (absolute == false) {
			return folder.list(new ExtensionNameFilter(extensions));
		}
		else {
			// return absolute path
			File[] files = folder.listFiles(new ExtensionFileFilter(extensions));

			String[] filePaths = new String[files.length];

			for (int i = 0; i < files.length; i++)
				filePaths[i] = files[i].getPath();
			return filePaths;
		}
		
	}

	public static class ExtensionNameFilter implements FilenameFilter {
		private String[] extensions;

		public ExtensionNameFilter(String[] exts) {
			extensions = exts;
		}

		public boolean accept(File dir, String name) {
			for (int i = 0; i < extensions.length; i++) {
				if (name.endsWith(extensions[i]))
					return true;
			}
			return false;
		}
	}


	public static class ExtensionFileFilter implements FileFilter {
		private String[] extensions;

		public ExtensionFileFilter(String[] exts) {
			extensions = exts;
		}

		public boolean accept(File pathname) {
			if (pathname.isFile() == false)
				return false;
			String name = pathname.getName();
			for (int i = 0; i < extensions.length; i++) {
				if (name.endsWith(extensions[i]))
					return true;
			}
			return false;
		}
	}


	public static String[] getFilePaths(String folderPath) {
		File folder = new File(folderPath);

		Collection<File> files = 
			org.apache.commons.io.FileUtils.listFiles(
						folder,
						new org.apache.commons.io.filefilter.WildcardFileFilter("*.java", org.apache.commons.io.IOCase.INSENSITIVE),
						org.apache.commons.io.filefilter.TrueFileFilter.TRUE
					);

		List<String> filePaths = new ArrayList<String>();

		for (File f : files) {
			filePaths.add(f.getPath());
		}

		return filePaths.toArray(new String[0]);
	}

	public static String[] getFolderPaths(String folderPath) {
		File folder = new File(folderPath);

		Collection<File> folders = 
			org.apache.commons.io.FileUtils.listFiles(
						folder,
						org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY,
						org.apache.commons.io.filefilter.TrueFileFilter.TRUE
					);

		List<String> folderPaths = new ArrayList<String>();

		for (File f : folders) {
			folderPaths.add(f.getPath());
		}

		folderPaths.add(folderPath);

		return folderPaths.toArray(new String[0]);
	}


	public static String[] getDirectSubFolderPaths(String rootPath) {
		// String rootPath = //"/Users/hans/Desktop/android.googlesource.com/platform/packages/apps";
		// 	"/Users/hans/Desktop/android/android-sdk-sample";

		String[] folders = 
			new File(rootPath).list(
						org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY);

		List<String> folderPaths = new ArrayList<String>();

		for (String folder : folders) {
			folderPaths.add(rootPath + "/" + folder);
		}

		return folderPaths.toArray(new String[0]);
	}


}