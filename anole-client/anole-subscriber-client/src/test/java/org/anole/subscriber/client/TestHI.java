package org.anole.subscriber.client;

public class TestHI {

	
	static Integer isHi(){
		System.out.println("Called");
		return 2;
	}
	
	public static void main(String[] args) {
		
		
		int a = isHi() == 2 ? isHi(): 0;
		
	}
	
}
