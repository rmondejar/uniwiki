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

import easypastry.util.UID;

import uniwiki.bean.WikiBean;
import uniwiki.concerns.DamonControl;
import uniwiki.concerns.distribution.Storage;

/**
 * @author Ruben Mondejar <ruben.mondejar@urv.cat>
 */
public class StorageTest {

	public static void main(String[] args) {

	  try {
		  
		if (args[0]!=null && args[0].length()>0) DamonControl.init(args[0]+File.separator+"damon-config.xml");
		else DamonControl.init("damon-config.xml");
		
		int num = Integer.parseInt(args[1]);

		WikiBean wiki = new WikiBean();		
		long t0,t1,At;
			
	    long saveMin,saveMax,saveAvg;
	    saveMin = Long.MAX_VALUE;
	    saveMax = Long.MIN_VALUE;
	    saveAvg = 0;
	    	
	    System.out.println("Starting Storage Task...");
	    for(int i=0;i<num;i++) {
	     
	     String pageName = UID.getUID(); 
		 t0 = System.currentTimeMillis();	
		 wiki.save(pageName, ""+i);
		 t1 = System.currentTimeMillis();
		 //System.out.println(" ("+(t1-t0)+" ms.) ");
		 
		 At = (t1-t0);
	     if (At>saveMax) saveMax = At;
	     if (At<saveMin) saveMin = At; 
	     saveAvg += At;	    	
	
	    }	    
	    saveAvg/=num;
	    
	    System.out.println("SAVE");
		System.out.println("max : "+saveMax);
		System.out.println("avg : "+saveAvg);
		System.out.println("min : "+saveMin);    	

		while(true) {
			  System.out.println("INFO");
			  System.out.println(DamonControl.getName()+"num : "+Storage.size()+"elems");
			  Thread.sleep(5000);
	    }
		
		/*		
	    System.out.println("Press any key to finish");
	    System.in.read();
	    
	    DamonControl.close();
	    System.exit(0);
	    */
	    
	} catch(Exception e) {e.printStackTrace();}  
	
	}
}
