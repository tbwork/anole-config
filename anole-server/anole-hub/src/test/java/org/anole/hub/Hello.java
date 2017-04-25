package org.anole.hub;

public class Hello {

	public static void main(String[] args) { 
		Boolean flag = true;
		
		Boolean flag2 = flag;
		
		System.out.println(flag == flag2);
		
		flag2 = !flag;
		 
		System.out.println(flag == flag2);
		
	}
	
}
