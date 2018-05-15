package org.tbwork.anole.loader.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class IOUtil {

	
	/**
	 * Transfer one InputStream to bytes. 
	 */
	public static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    }  
	 
	
	/**
	 * Copy a stream from a file in one jar.
	 */
	public static InputStream getCopiedInputStream(JarFile jf, JarEntry je) {
		try {
			byte [] tempBytes = input2byte(jf.getInputStream(je));
			return new ByteArrayInputStream(tempBytes);
		}
		catch(Exception e) {
			// never goes here if the previous file is ok 
			return null;
		}
	}
	
	/**
	 * Get the stream's reference of a file in one jar.
	 */
	public static InputStream getInputStream(JarFile jf, JarEntry je) {
		try { 
			return jf.getInputStream(je);
		}
		catch(Exception e) {
			// never goes here if the previous file is ok 
			return null;
		}
	}
	
	 
	public static InputStream getZipInputStream(InputStream in, ZipEntry entry)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		long size = entry.getSize();
		if (size > -1) {
			byte[] buffer = new byte[1024 * 4];
			int n = 0;
			long count = 0;
			while (-1 != (n = in.read(buffer)) && count < size) {
				baos.write(buffer, 0, n);
				count += n;
			}
		} else {
			while (true) {
				int b = in.read();
				if (b == -1) {
					break;
				}
				baos.write(b);
			}
		}  
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
