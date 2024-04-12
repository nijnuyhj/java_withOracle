package java_with_oracle.project;

import lombok.Data;

@Data
public class Book {
	private int bookid;
	private String title;
	private String genre;
	private String location;
	private int remain;
}
