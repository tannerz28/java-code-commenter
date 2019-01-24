public class Comment {
	String value;
	int index;
	String className;
	String superClassName;
	
	public Comment (String value, int index, String className, String superClassName) {
		this.value = value;
		this.index = index;
		this.className = className;
		this.superClassName = superClassName;
	}
}