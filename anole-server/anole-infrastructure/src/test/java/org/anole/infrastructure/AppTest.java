package org.anole.infrastructure;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest  
{
     
	public static interface IS{
		public int sub();
	}
	
	public static class SS implements IS{

		int a ;
		@Override
		public int sub() {
			return --a;
		}
		
	}
	
	public static <D, S extends IS> D subS(S s){
		 return (D) Integer.valueOf(s.sub()).toString();
		 
	}
	
	public static void main(String[] args) {
		 SS ss = new SS();
		 ss.a = 10;
		 String b = subS(ss);
		 System.out.println(b);
	}
}
