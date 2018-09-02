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
import uniwiki.concerns.distribution.Storage;

/**
 * @author Ruben Mondejar <ruben.mondejar@urv.cat>
 */
public class SaveLoadTest {

	public static void main(String[] args) {

	  try {
		  
		if (args[0]!=null && args[0].length()>0) DamonControl.init(args[0]+File.separator+"damon-config.xml");
		else DamonControl.init("damon-config.xml");
		
		int num = Integer.parseInt(args[1]);

		WikiBean wiki = new WikiBean();
		String pageName = "test";
		String pageContent = "hello world!";
		long t0,t1;
					
		System.out.print("Saving <"+pageName+"> with :["+pageContent+"]");
		t0 = System.currentTimeMillis();	
	    wiki.save(pageName, pageContent);
	    t1 = System.currentTimeMillis();
	    System.out.println(" ("+(t1-t0)+" ms.) ");
	    
	    long saveMin,saveMax,saveAvg;
	    long loadMin,loadMax,loadAvg;
	    loadMin = saveMin = Long.MAX_VALUE;
	    loadMax = saveMax = Long.MIN_VALUE;
	    loadAvg = saveAvg = 0;
	    	    
	    for(int i=0;i<num;i++) {
	    	
	     //System.out.print("Loading <"+pageName+">");
		 t0 = System.currentTimeMillis();	
		 String session = wiki.load(pageName);
		 t1 = System.currentTimeMillis();
		 //System.out.println(" ("+(t1-t0)+" ms.) ");
		 //System.out.println("Result : "+session);
				 
		 long At = (t1-t0);
	     if (At>loadMax) loadMax = At;
	     if (At<loadMin) loadMin = At; 
	     loadAvg += At;   
	     
	     //System.out.print("Saving <"+pageName+"> with :["+pageContent+"]");
		 t0 = System.currentTimeMillis();	
		 wiki.save(pageName, session+""+i);
		 t1 = System.currentTimeMillis();
		 //System.out.println(" ("+(t1-t0)+" ms.) ");
		 
		 At = (t1-t0);
	     if (At>saveMax) saveMax = At;
	     if (At<saveMin) saveMin = At; 
	     saveAvg += At;	    	
	
	    }
	    loadAvg/=num;
	    saveAvg/=num;
	    
	    System.out.println("LOAD");
	    System.out.println("max : "+loadMax);
	    System.out.println("avg : "+loadAvg);
	    System.out.println("min : "+loadMin);
	
	    System.out.println("SAVE");
		System.out.println("max : "+saveMax);
		System.out.println("avg : "+saveAvg);
		System.out.println("min : "+saveMin);    	
		
		while(true) {
		  System.out.println("INFO");
		  System.out.println(DamonControl.getName()+"num : "+Storage.size()+"elems");
		  Thread.sleep(5000);
		}  

	    //System.out.println("Press any key to finish");
	    //System.in.read();
	    
	    //DamonControl.close();
	    //System.exit(0);
	    
	} catch(Exception e) {e.printStackTrace();}  
	
	}
}
