package m2515029;

public class Article extends Publication {
	int _beginPage;
	int _endPage;
	
	public Article(String title) {
		super(title);
	}

	public Article(String title, int year) {
		super(title,year);
	}
	
	public int getBeginPage(){
		return _beginPage;
	}
	void setBeginPage(int beginPage){
		_beginPage = beginPage;
	}
	public int getEndPage(){
		return _endPage;
	}
	void setEndPage(int endPage){
		_endPage = endPage;
	}
}
