import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Path path = getPathFromUser();

		File fileOrDirectory = new File(path.toString());

		if (!fileOrDirectory.isDirectory()) {
			commentFile(fileOrDirectory);
			return;
		}

		ArrayList<File> files = getFilesInDirectory(fileOrDirectory);

		for (File file : files) {
			commentFile(file);
		}
	}

	private static ArrayList<File> getFilesInDirectory(@NotNull File folder) {
		ArrayList<File> files = new ArrayList<>();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				files.addAll(getFilesInDirectory(fileEntry));
			} else {
				files.add(fileEntry);
			}
		}

		return files;
	}

	private static void commentFile(@NotNull File file) {
		if (file.getName().contains("-commented") || !file.getName().endsWith(".java")) {
			return;
		}

		List<String> lines;
		ArrayList<Comment> comments = new ArrayList<>();

		try {
			lines = Files.readAllLines(file.toPath());

			initializeComments(comments, lines);

			writeNewFile(file.getAbsolutePath(), lines, comments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Path getPathFromUser() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the absolute path of the file or directory containing the .java files: ");
		String path = scanner.nextLine();
		scanner.close();
		return Paths.get(path);
	}
	
	private static void initializeComments(ArrayList<Comment> comments, @NotNull final List<String> lines) {
		String className = null;
		String superClassName = null;
		for (int i = 0; i < lines.size(); i++) {
			for(CommentLambda lambda : getCommentLambdas()) {
				Comment comment = lambda.run(lines.get(i), i, lines, className, superClassName);
				if(comment != null) {
					className = comment.className;
					superClassName = comment.superClassName;
					comments.add(comment);
				}
			}
		}
	}
	
	private static void writeNewFile(String path, List<String> lines, ArrayList<Comment> comments) {
		try {
			FileWriter writer = new FileWriter(path.replace(".java", "-commented.java"));
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				for(Comment comment : comments) {
					if (comment.index == i) {
						int numberOfSpaces = 0;
						while(true) {
							for (char c : line.toCharArray()) {
								if (c == ' ') {
									numberOfSpaces++;
								} else {
									break;
								}
							}
							break;
						}
						String commentString = "";
						for (int t = 0; t < numberOfSpaces; t++) {
							commentString += ' ';
						}

						commentString += "// " + comment.value + "\r\n";

						writer.write(commentString);
					}
				}
		        writer.write(line + "\r\n");
			}
			writer.close();
	    } catch (Exception e) {
	    	e.printStackTrace(System.out);
	    }
	}  
	
	@NotNull
	@Contract(value = " -> new", pure = true)
	private static CommentLambda[] getCommentLambdas() {
		return new CommentLambda[] {
			/*
			* Comment the Main/Driver class
			* */
			(line, i, lines, nullClassName, nullSuperClassName) -> {
				if (line.contains("class ") && (line.contains("Main") || line.contains("Driver"))) {
					String dirtyClass = line.substring(line.indexOf("class ")).replace("class ", "");
					String className = dirtyClass.substring(0, dirtyClass.indexOf(" "));
					return new Comment("The driver class", i, className, null);
				} else {return null;}
			}
			/*
			* Comment subclasses
			* */
			, (line, i, lines, nullClassName, nullSuperClassName) -> {
				if (line.contains("class ") && line.contains("extends ")) {
					String dirtyParentClass = line.substring(line.indexOf("extends ")).replace("extends ", "");
					String parentClass = dirtyParentClass.substring(0, dirtyParentClass.indexOf(" "));
				 	String dirtyClass = line.substring(line.indexOf("class ")).replace("class ", "");
				 	String className = dirtyClass.substring(0, dirtyClass.indexOf(" "));
					return new Comment("Subclass of " + parentClass, i, className, parentClass);
				} else {return null;}
			}
			/*
			* Comment classes that are neither subclasses nor the Main class
			* */
			, (line, i, lines, nullClassName, nullSuperClassName) -> {
				if(line.contains("class ") && !line.contains("extends ") && !line.contains("Main") && !line.contains("Driver")) {
					String dirtyClass = line.substring(line.indexOf("class ")).replace("class ", "");
					String className = dirtyClass.substring(0, dirtyClass.indexOf(" "));
					return new Comment("Parent class", i, className, null);
				} else {return null;}
			}
			/*
			* Comment the main method
			* */
			, (line, i, lines, className, superClassName) -> {
				if (line.contains("static void main")) {
					return new Comment("The program's main method", i, className, superClassName);
				} else {
					return null;
				}
			}
			/*
			* Comment the constructor
			* */
			, (line, i, lines, className, superClassName) -> {
				if(line.contains("public " + className +"(")) {
					return new Comment("Constructor of the " + className + " class, creates an instance of the class", i, className, superClassName);
				} else {return null;}
			}
			/*
			* Comment super call in constructor
			* */
			, (line, i, lines, className, superClassName) -> {
				if (line.contains("super(")) {
					return new Comment("Calls the constructor of the parent " + superClassName + " class", i, className, superClassName);
				} else {return null;}
			}
		};
	}
}

