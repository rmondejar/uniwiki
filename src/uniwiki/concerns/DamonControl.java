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
package uniwiki.concerns;

import damon.core.*;
import damon.reflection.MetaData;
import easypastry.dht.DHTException;
import java.io.IOException;

import org.jdom.JDOMException;

/**
 * @author Ruben Mondejar <ruben.mondejar@urv.cat>
 */
public class DamonControl {
	
  private static String groupName = "p2p://uniwiki";
  private static String[] aspects = {
	                          "uniwiki.concerns.consistency.Integrator",
		                      "uniwiki.concerns.consistency.Editor",		  					  
		  					  "uniwiki.concerns.replication.ReplicaStore",
		  					  "uniwiki.concerns.replication.Replicator",
		  					  "uniwiki.concerns.distribution.Storage",
		  					  "uniwiki.concerns.distribution.Locator"  					  
		  					  };
	
  public static void init (String xmlConfigFile) {
    
    try {
        DamonCore.init(xmlConfigFile);     
        DamonCore.setClassLoader(ClassLoader.getSystemClassLoader()); 
		DamonCore.registerGroup(groupName);
		deployDistributedAspects(aspects);
		
                        
    } catch (Exception e) {
		
      System.out.println ("------------------------------ EXCEPTION --------------------------------");
      System.out.println ("-------------------------- Initializing Damon ----------------------------");
      System.out.println ("-------------------------------------------------------------------------");
      e.printStackTrace();      
    }
  }
  
  private static void deployDistributedAspects(String[] aspects) throws Exception {
   	    
		for(String aspectName : aspects) {		
						
		  Class aspectClass = Thread.currentThread().getContextClassLoader().loadClass(aspectName);
		  
  	      //DamonCore.getStorage().deploy(aspectName,aspectClass);
  	        	      
  	      MetaData md;
  	      //if (is!=null) 
  	    	  md = DamonCore.getControl().getMetaData(aspectName,Thread.currentThread().getContextClassLoader());
  	      //else md = DamonCore.getControl().getMetaData(aspectName);
  	      
  	      //System.out.println("Metadata ("+aspectName+") : "+md);
  	      md.setScope(groupName);  	    
  	      md.setClassLoader(Thread.currentThread().getContextClassLoader());
          DamonCore.getControl().activateMetaData(md);          
		}
  }  

  public static void close() {             
        try {
			DamonCore.getControl().passivateLocallyAll(groupName);
		} catch (DHTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        DamonCore.close();     
    
  }

  public static String getName() {	
	return DamonCore.getHostName();
  }
}
