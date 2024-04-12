package java_with_oracle.project;

import lombok.Data;

@Data
public class Member {
	private int memberid;
	private String nickname;
	private String password;
	private String name;
	private String major;
	private int grade;
	private int manager; 
}
