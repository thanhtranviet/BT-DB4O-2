package m2515029;

import java.io.File;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Constraint;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class Main {
	static String DB_NAME = "persons.db4o";

	@SuppressWarnings("serial")
	public static void main(String[] args) {

		// 1. Xoá CSDL nếu CSDL đã có
		new File(".", DB_NAME).delete();

		ObjectContainer db = null;
		// 2. Tạo mới và mở CSDL
		try {
			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB_NAME);
			/**
			 * 3. Tạo publication ("Fundamentals of Database Systems", 2015) với
			 * các tác giả : ("Ramez Elmasri"); ("Shamkant B. Navathe"); *
			 */
			Publication fundamentals = new Publication("Fundamentals of Database Systems", 2015);
			Author ramez = new Author("Ramez Elmasri");
			Author shamkant = new Author("Shamkant B. Navathe");
			fundamentals.addAuthor(ramez);
			fundamentals.addAuthor(shamkant);
			ramez.addPub(fundamentals);
			shamkant.addPub(fundamentals);
			// 4. Lưu vào CSDL publication vừa tạo, chi luu Pub khong luu tac
			// gia
			db.store(fundamentals);
			// 5. Truy vấn QBE : tìm tác giả Ramez Elmasri và cho nhận xét
			System.out.println("5//------- QBE : Author -----------");
			ObjectSet<Author> authors = db.queryByExample(new Author("Ramez Elmasri"));
			// In kết quả
			for (Author author : authors) {
				System.out.println(author.getName());
			}

			// 6. Hiển thị tất cả Publication
			System.out.println("6//----------- QBE : All Publications ---------------");

			ObjectSet<Publication> pubs = db.queryByExample(new Publication(null));
			for (Publication publication : pubs) {
				System.out.println(publication.getTitle());
			}
			// Native
			System.out.println("---------NATIVE & simple  & comparision ------------");
			/**
			 * 7. Thêm vào CSDL 2 publication "Zend Framework 1 to 2 Migration
			 * Guide: a php[architect] guide 2015 - Tac gia : Bart McLeod va Eli
			 * White" va "Big Data For Dummies 2013- tac gia: Arun Murthy va
			 * Vinod Vavilapalli" *
			 */
			System.out.println("7//--------------");
			Publication zend = new Publication("Zend Framework 1 to 2 Migration Guide: a php[architect] guide", 2015);
			Author bart = new Author("Bart McLeod");
			Author eli = new Author("Eli White");
			zend.addAuthor(bart);
			zend.addAuthor(eli);
			Publication big = new Publication("Big Data For Dummies", 2013);
			Author arun = new Author("Arun Murthy");
			Author vinod = new Author("Vinod Vavilapalli");
			big.addAuthor(arun);
			big.addAuthor(vinod);

			db.store(zend);
			db.store(big);

			// 8.Tìm Tất cả Publication từ năm 1990 trở về sau và sắp xếp theo
			// thứ tự theo tên bài báo
			System.out.println("8//--------------");
			Comparator<Publication> personCmp = new Comparator<Publication>() {
				public int compare(Publication p1, Publication p2) {
					return p1.getTitle().compareTo(p2.getTitle());
				}
			};
			ObjectSet<Publication> results = db.query(new Predicate<Publication>() {
				public boolean match(Publication publication) {
					return publication.getYear() >= 1990;
				}
			}, personCmp);
			for (Publication publication : results) {
				System.out.println(String.format("Title: %s, Year %s", publication.getTitle(), publication.getYear()));
			}
			// 9. Tìm Tất cả Publication từ năm 1990 đến năm 2013 hoặc có tựa là
			// "Java Programming"
			System.out.println("9//---------native & complex  ------------");
			results = db.query(new Predicate<Publication>() {
				public boolean match(Publication publication) {
					return publication.getYear() >= 1990 && publication.getYear() <= 2013
							|| publication.getTitle().equals("Java Programming");
				}
			}, personCmp);
			for (Publication publication : results) {
				System.out.println(String.format("Title: %s, Year %s", publication.getTitle(), publication.getYear()));
			}

			/**
			 * *** SODA***
			 */
			System.out.println("---------SODA------------");
			// 10. Tìm Tất cả Publication từ năm 1990 và có tựa là "Java
			// Programming"
			System.out.println("10//---------native & complex  ------------");

			Query query = db.query();
			query.constrain(Publication.class);
			Constraint constr = query.descend("year").constrain(1990).greater();
			query.descend("title").constrain("Java Programming").and(constr);
			results = query.execute();
			for (Publication p : results) {
				System.out.println(String.format("Title: %s, Year: %s", p.getTitle(), p.getYear()));
			}

			/**
			 * ** UPDATE SIMPLE***********
			 */
			// 11. Cập nhật lại ngày sinh cho tác giả Ramez Elmasri là
			// 20/10/1956
			System.out.println("11//---------native & complex  ------------");
			ObjectSet<Author> rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				Calendar calendar = Calendar.getInstance();
				calendar.set(1956, Calendar.OCTOBER, 20);
				au.setBirthday(calendar.getTime());
				db.store(au);
			}
			// 12. HIển thị kết quả cập nhật
			System.out.println("12//---------native & complex  ------------");
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				System.out.println(String.format("Name: %s, Bithday: %s", au.getName(), au.getBirthday()));
			}
			
			/**
			 * ****** UPDATE COMPLEX*************
			 */
			// 13. Cap nhat lai ngay sinh cho tác giả Ramez Elmasri 20/10/1945
			// và năm xuất bản cho tất cả các publication là 2008
			System.out.println("13//---------native & complex  ------------");
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				Calendar calendar = Calendar.getInstance();
				calendar.set(1945, Calendar.OCTOBER, 20);
				au.setBirthday(calendar.getTime());
				for (Publication p : au.getPubs()) {
					p.setYear(2008);
				}
				db.store(au);
			}
			
			// 14. In kết quả gồm tên năm sinh tựa và năm xuất bản các pub của
			// tác giả vừa cập nhật để kiểm tra
			System.out.println("14//---------native & complex  ------------");
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				System.out.println(String.format("Name: %s, Bithday: %s", au.getName(), au.getBirthday()));
				for (Publication p : au.getPubs()) {
					System.out.println(String.format("Title: %s, Year: %s", p.getTitle(), p.getYear()));
				}
			}
			

			// 15. Đóng CSDL sau đó mở lại
			System.out.println("16//---------close & reopen db  ------------");
			db.close();
			db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB_NAME);
			// 16. Làm lại câu 14 và nhận xét
			System.out.println("16//---------native & complex  ------------");
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				System.out.println(String.format("Name: %s, Bithday: %s", au.getName(), au.getBirthday()));
				for (Publication p : au.getPubs()) {
					System.out.println(String.format("Title: %s, Year: %s", p.getTitle(), p.getYear()));
				}
			}
			// 17. Đặt lại cấu hình cho phép cập nhật cascade.
			System.out.println("16//---------close & reopen db with cascade config  ------------");
			db.close();
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.common().objectClass(Author.class).cascadeOnUpdate(true); 
			db = Db4oEmbedded.openFile(config, DB_NAME);
			// 18. Chạy lại chương trình để kiểm tra cập nhật cascade
			System.out.println("18//---------native & complex  ------------");
			//update
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				Calendar calendar = Calendar.getInstance();
				calendar.set(1945, Calendar.OCTOBER, 20);
				au.setBirthday(calendar.getTime());
				for (Publication p : au.getPubs()) {
					p.setYear(2008);
				}
				db.store(au);
			}
			//print result			
			rs = db.queryByExample(new Author("Ramez Elmasri"));
			if (rs.hasNext()) {
				Author au = (Author) rs.next();
				System.out.println(String.format("Name: %s, Bithday: %s", au.getName(), au.getBirthday()));
				for (Publication p : au.getPubs()) {
					System.out.println(String.format("Title: %s, Year: %s", p.getTitle(), p.getYear()));
				}
			}
			// 19. Thêm hai class java định nghĩa cho Article và Book kế thừa
			// Publication như hình trong phần giới thiệu db4O
			System.out.println("19//---------native & complex add book and article  ------------");
			// 20.Thêm Quyển sách "Gone with the wind", tác giả Margaret
			// Mitchell, năm xuất bản 2011, Giá 12.98
			System.out.println("20//---------native & complex add gone with the wind book  ------------");
			Book gone = new Book("Gone with the wind", 2011);
			gone.setPrice(12.18);
			Author au = new Author("Margaret Mitchell");
			gone.addAuthor(au);
			au.addPub(gone);
			db.store(gone);
			// 21. Kiểm tra rằng tác giả và publication cũng được lưu
			//print result			
			rs = db.queryByExample(new Author("Margaret Mitchell"));
			if (rs.hasNext()) {
				Author au1 = (Author) rs.next();
				System.out.println(String.format("Name: %s, Bithday: %s", au1.getName(), au1.getBirthday()));
				for (Publication p : au1.getPubs()) {
					System.out.println(String.format("Title: %s, Year: %s", p.getTitle(), p.getYear()));
				}
			}
			db.close();
		} finally {
			if (db != null)
				db.close();
		}
	}

}
