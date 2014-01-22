package astparser;

public class FileUtils {
	/*
		FileUtils.getJavaFiles("/Users/hans/Desktop/ast/astparser", 
					new String[] {".java", ".JAVA"}),
	*/
	public static String[] getFiles(String path, String[] extensions) {
		File folder = new File(path);
		String[] sourceFilePaths = folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				for (int i = 0; i < extensions.length; i++) {
					if (name.endsWith(extensions[i])
						return true;
				}
				return false;
			}
		});

		return sourceFilePaths;
	}
}