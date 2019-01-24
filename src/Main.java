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
	
	private static void initializeComments(ArrayList<Comment> comments, final List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			for(CommentLambda lambda : getCommentLambdas()) {
				Comment comment = lambda.run(lines.get(i), i);
				if(comment != null) {
					comments.add(comment);
				}
			}
		}
	}
	
	private static void writeNewFile(String path, List<String> lines, ArrayList<Comment> comments) {
		try {
			FileWriter writer = new FileWriter(path.toString().replace(".java", "-commented.java")); 
			for (int i = 0; i < lines.size(); i++) {
				for(Comment c : comments) {
					if (c.index == i) {
						writer.write("// " + c.value + "\r\n");	
					}
				}
		        String newLine =  lines.get(i);
		        writer.write(newLine + "\r\n");
			}
			writer.close();
	    } catch (Exception e) {
	    	e.printStackTrace(System.out);
	    }
	}  
	
	private static CommentLambda[] getCommentLambdas() {
		return new CommentLambda[] {
			(line, i) -> {
				 if (line.contains("class ") && line.contains("extends ")) {
					String dirtyParentClass = line.substring(line.indexOf("extends ")).replace("extends ", "");
					String parentClass = dirtyParentClass.substring(0, dirtyParentClass.indexOf(" "));
					return new Comment("Subclass of " + parentClass, i); 
				} else {return null;}
			}
			, (line, i) -> {
				if(line.contains("class ") && !line.contains("extends ")) {
					return new Comment("Parent class", i);
				} else {return null;}
			}
			, (line, i) -> {
				if (line.contains("static void main(String[]")) {
					return new Comment("The program's main method.", i);
				} else {return null;}
			}
		};
	}
}

