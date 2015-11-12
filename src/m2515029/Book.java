package m2515029;

import java.util.ArrayList;

public class Book extends Publication {
	double _price;
	
	public Book(String title) {
		super(title);
	}

	public Book(String title, int year) {
		super(title,year);
	}
	
	public double getPrice(){
		return _price;
	}
	
	void setPrice(double price){
		_price = price;
	}
}
