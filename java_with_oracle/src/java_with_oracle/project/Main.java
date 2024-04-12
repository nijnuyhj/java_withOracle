package java_with_oracle.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {
	private Scanner sc = new Scanner(System.in);
	private Connection conn;
	private int userInfo;

	public Main() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "testuser", "test1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void page1() {
		System.out.println();
		System.out.println("[회원가입 페이지]");
		System.out.println("-----------------------------------------------------");
		page2();
	}

	public void page2() {
		System.out.println();
		System.out.println("1.회원가입 | 2.로그인");
		System.out.println();
		System.out.println("-----------------------------------------------------");
		System.out.println();
		System.out.println("선택: ");
		String oneortwo = sc.nextLine();
		System.out.println();

		switch (oneortwo) {
		case "1":
			signup();
		case "2":
			login();
		default:
			page2();
		}
	}

	public void page3() {
		System.out.println();
		System.out.println("[학생 페이지]");
		System.out.println("1.도서검색 | 2.대출하기");
		System.out.println("-----------------------------------------------------");
		System.out.println();
		System.out.println("선택: ");
		String select = sc.nextLine();
		System.out.println();

		switch (select) {
		case "1":
			list();
		case "2":
			lend();
		default:
			page3();
		}
	}

	public void page4() {
		System.out.println();
		System.out.println("[관리자 페이지]");
		System.out.println("1.책 등록 | 2.책 조회 | 3.책 삭제");
		System.out.println("-----------------------------------------------------");
		System.out.println();
		System.out.println("선택: ");
		String select = sc.nextLine();
		System.out.println();

		switch (select) {
		case "1":
			create();
		case "2":
			show();
		case "3":
			delete();
		default:
			page4();
		}
	}

	public void create() {
		Book book = new Book();
		System.out.println("책 제목: ");
		book.setTitle(sc.nextLine());
		System.out.println("장르: ");
		book.setGenre(sc.nextLine());
		System.out.println("책 위치: ");
		book.setLocation(sc.nextLine());
		System.out.println("잔여 도서: ");
		book.setRemain(sc.nextInt());

		try {
			String sql = "" + "INSERT INTO books(bookid,memberid,title,genre,location,remain)"
					+ "VALUES (SEQ_BNO.NEXTVAL,?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userInfo);
			pstmt.setString(2, book.getTitle());
			pstmt.setString(3, book.getGenre());
			pstmt.setString(4, book.getLocation());
			pstmt.setInt(5, book.getRemain());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		page4();
	}

	public void show() {
		try {
			String sql = "" + "SELECT * FROM books";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("도서 목록");
			System.out.println("----------------------------------------------------------------");
			System.out.printf("%-19s | %-14s | %-9s | %-10s%n", "제목", "장르", "위치", "잔여 도서");
			System.out.println("----------------------------------------------------------------");

			while (rs.next()) {
				String title = rs.getString("title");
				String genre = rs.getString("genre");
				String location = rs.getString("location");
				int remain = rs.getInt("remain");

				System.out.printf("%-20s | %-15s | %-10s | %-10d%n", title, genre, location, remain);
			}
			System.out.println("------------------------------------------------------------------");
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		page4();
	}

	public void delete() {
		try {
			System.out.println("삭제할 책 제목: ");
			String title = sc.nextLine();

			String sql = "DELETE FROM books WHERE title=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("도서목록에서 삭제되었습니다.");

	}

	public void list() {
		System.out.println("[도서 검색]");
		System.out.print("책 제목: ");
		String title = sc.nextLine();

		try {
			String sql = "" + "SELECT bookid, title, genre, location, remain FROM books " + "WHERE title =?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				Book book = new Book();
				book.setTitle(rs.getString("title"));
				book.setGenre(rs.getString("genre"));
				book.setLocation(rs.getString("location"));
				book.setRemain(rs.getInt("remain"));
				System.out.println("------------------------------------");
				System.out.println("제목: " + book.getTitle());
				System.out.println("장르: " + book.getGenre());
				System.out.println("장소: " + book.getLocation());
				System.out.println("남은 도서: " +book.getRemain());
				System.out.println("------------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		page3();
	}

	public void lend() {
		System.out.println("[대출할 도서]");
		System.out.print("책 제목: ");
		String title = sc.nextLine();
		
		try {
			String sql = "" + "SELECT remain FROM books WHERE title=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, title);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				int remain = rs.getInt("remain");
				if(remain>0) {
					String update = "UPDATE books SET remain=? WHERE title=?";
					PreparedStatement pstmt2 = conn.prepareStatement(update);
					pstmt2.setInt(1,remain-1);
					pstmt2.setString(2, title);
					pstmt2.executeUpdate();
	                System.out.println("도서가 대출되었습니다.");
				}else if(remain==0) {
					System.out.println("모든 도서가 대출 중입니다.");
				}else {
					System.out.println("해당 도서는 존재하지 않습니다.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		page3();
	}

	public void signup() {
		Member member = new Member();
		System.out.println("[회원가입]");
		System.out.println("닉네임: ");
		member.setNickname(sc.nextLine());
		System.out.println("비밀번호: ");
		member.setPassword(sc.nextLine());
		System.out.println("이름: ");
		member.setName(sc.nextLine());
		System.out.println("전공: ");
		member.setMajor(sc.nextLine());
		System.out.println("학년: ");
		member.setGrade(sc.nextInt());
		System.out.println("관리자여부: ");
		member.setManager(sc.nextInt());

		System.out.println("--------------------------------------------------------");
//		System.out.println("1.Success | 2.Cancel");
//		String oneortwo = sc.nextLine();

//		if(oneortwo.equals("1")) {
		try {
			String sql = "" + "INSERT INTO members (memberid,nickname,password,name,major,grade,manager)"
					+ "VALUES (SEQ_BNO.NEXTVAL, ?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getNickname());
			pstmt.setString(2, member.getPassword());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getMajor());
			pstmt.setInt(5, member.getGrade());
			pstmt.setInt(6, member.getManager());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		page2();
	}
//		page2();

	public void login() {
		System.out.println("[로그인]");
		System.out.println("닉네임: ");
		String nickname = sc.nextLine();
		System.out.println("비밀번호: ");
		String password = sc.nextLine();

		try {
			String sql = "SELECT * FROM members WHERE nickname = ? AND password = ? ";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nickname);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				System.out.println("로그인에 성공하셨습니다.");
				userInfo = rs.getInt("memberid");
//                Main main =new Main();
				int manager = rs.getInt("manager");
				if (manager == 0) {
					page3();
				} else {
					page4();
				}
			} else {
				System.out.println("로그인에 실패하셨습니다.");
				Main main = new Main();
				page1();
			}

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		main.page1();

	}

}
