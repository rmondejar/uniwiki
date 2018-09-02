/*****************************************************************************************
 * UniWiki
 * Copyright (C) 2008 URV - INRIA Nancy-Loria
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************************/
package uniwiki.test;

import java.io.File;

import uniwiki.bean.WikiBean;
import uniwiki.concerns.DamonControl;

/**
 * @author Ruben Mondejar <ruben.mondejar@urv.cat>
 */
public class SimpleTest {

	public static void main(String[] args) {

	  try {
		  
		if (args[0]!=null) DamonControl.init(args[0]+File.separator+"damon-config.xml");
		else DamonControl.init("damon-config.xml");

		WikiBean wiki = new WikiBean();
		String pageName = "test";
		String pageContent = "hello world!";
		long t0,t1;
					
		System.out.print("Saving <"+pageName+"> with :["+pageContent+"]");
		t0 = System.currentTimeMillis();	
	    wiki.save(pageName, pageContent);
	    t1 = System.currentTimeMillis();
	    System.out.println(" ("+(t1-t0)+" ms.) ");
	    
	    System.out.print("Loading <"+pageName+">");
	    t0 = System.currentTimeMillis();	
	    String session = wiki.load(pageName);
	    t1 = System.currentTimeMillis();
	    System.out.println(" ("+(t1-t0)+" ms.) ");
	    System.out.println("Result : "+session);
	    
	    pageContent = session + " again";
	    System.out.print("Saving <"+pageName+"> with :["+pageContent+"]");
	    t0 = System.currentTimeMillis();
	    wiki.save(pageName, pageContent);
	    t1 = System.currentTimeMillis();
	    System.out.println(" ("+(t1-t0)+" ms.) ");
	    
	    System.out.print("Loading <"+pageName+">");
	    t0 = System.currentTimeMillis();
	    session = wiki.load(pageName);
	    t1 = System.currentTimeMillis();
	    System.out.println(" ("+(t1-t0)+" ms.) ");
	    System.out.println("Result : "+session);
	

	    System.out.println("Press any key to finish");
	    System.in.read();
	    
	    DamonControl.close();
	    System.exit(0);
	    
	} catch(Exception e) {e.printStackTrace();}  
	
	}
}
