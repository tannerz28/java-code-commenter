import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
		List<String> lines;
		ArrayList<Comment> comments = new ArrayList<>();
		
		try {
			lines = Files.readAllLines(path);
			
			initializeComments(comments, lines);
			
			writeNewFile(path.toString(), lines, comments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Path getPathFromUser() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the full file path: ");
		String path = scanner.next();
		scanner.close();
		return Paths.get(path);
	}
	
	private static void initializeComments(ArrayList<Comment> comments, @NotNull final List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			for(CommentLambda lambda : getCommentLambdas()) {
				Comment comment = lambda.run(lines.get(i), i, lines);
				if(comment != null) {
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
			(line, i, lines) -> {
				if (line.contains("class ") && (line.contains("Main") || line.contains("Driver"))) {
					return new Comment("The driver class", i);
				} else {return null;}
			}
			/*
			* Comment subclasses
			* */
			, (line, i, lines) -> {
				 if (line.contains("class ") && line.contains("extends ")) {
					String dirtyParentClass = line.substring(line.indexOf("extends ")).replace("extends ", "");
					String parentClass = dirtyParentClass.substring(0, dirtyParentClass.indexOf(" "));
					return new Comment("Subclass of " + parentClass, i); 
				} else {return null;}
			}
			/*
			* Comment classes that are neither subclasses nor the Main class
			* */
			, (line, i, lines) -> {
				if(line.contains("class ") && !line.contains("extends ") && !line.contains("Main") && !line.contains("Driver")) {
					return new Comment("Parent class", i);
				} else {return null;}
			}
			/*
			* Comment the main method
			* */
			, (line, i, lines) -> {
				if (line.contains("static void main")) {
					return new Comment("The program's main method", i);
				} else {return null;}
			}
		};
	}
}

