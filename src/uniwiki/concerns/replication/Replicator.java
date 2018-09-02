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
package uniwiki.concerns.replication;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import uniwiki.concerns.distribution.Storage;

import damon.annotation.*;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;
import damon.reflection.ReflectionHandler;
import damon.reflection.thisEndPoint;
import damon.util.Utilities;
import damon.util.collections.SetHashtable;

/**
 * Distributed Meta-Aspect Replicator
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect (abstraction = Abstractions.LOCAL, target = "p2p://uniwiki")
public class Replicator extends DistributedMetaAspect {	
	
	public static final int R_FACTOR = 4;
	private ReflectionHandler rh = thisEndPoint.getReflectionHandler();
	
	private static SetHashtable<NodeHandle,String> replicators = new SetHashtable<NodeHandle,String>();
	private static SetHashtable<String,NodeHandle> replicas = new SetHashtable<String,NodeHandle>();
	
	public static Set<NodeHandle> getReplicaHosts() {
		Set<NodeHandle> total = new HashSet<NodeHandle>();
		for (Set<NodeHandle> set : replicas.values()) {
			total.addAll(set);
		}
		return total;
	}
	
	@RemoteMetaPointcut(id = "put", type = Type.AFTER)    
	public void onPut(RemoteJoinPoint rjp, String key, Object value) {
		Id id =  Utilities.generateHash(key);		
		System.out.println("Replicator [replicate] : "+key);		
		if (Storage.containsKey(id)) redoReplicas(key,value);
		checkNumReplicas(key,value);
	}
	
	@RemotePointcut(id = "replicate", abstraction = Abstractions.DIRECT, synchro = true)
	public void redoReplicas(String key, Object value) {			
		    
		    Id id =  Utilities.generateHash(key);
		    Object oldValue = Storage.getValue(id);
		    
		    if (!oldValue.equals(value)) {
		    	
			  Set<NodeHandle> replicatorsById = replicas.get(key);
			  Set<NodeHandle> blacklist = new HashSet<NodeHandle>();
		      for(NodeHandle nh : replicatorsById) {				  
				  Object res = super.invoke("redoReplicas",null,nh, new Object[]{key,value});
				  if (res==null) {					  
					  blacklist.add(nh); //cancel replicator (indirect)
				  }
			  }		
		    
		      for (NodeHandle nh : blacklist) {
		        replicators.remove(nh,key);		
			    replicas.remove(key, nh);
		      }
		    }  
	}
	
	@RemotePointcut(id = "replicate", abstraction = Abstractions.DIRECT, synchro = true)
	public void checkNumReplicas(String key, Object value) {
					    
		    //Id id =  Utilities.generateHash(key);
			Set<NodeHandle> replicatorsById = replicas.get(key);
			int num = replicatorsById.size();
		    		    
			
			if (num < R_FACTOR) {
				
		      Set<NodeHandle> newers = new HashSet<NodeHandle>(rh.getNeighbours(false));
		      newers.removeAll(replicatorsById);
		    
			  Iterator<NodeHandle> it = newers.iterator();				
			  while(num < R_FACTOR && it.hasNext()) {
			    NodeHandle nh = it.next();
			    //System.out.println("Replica to : "+nh);			    
			    Object res = null;
			    System.out.println("New replica ("+num+"): "+nh);
			    while(res==null && nh.isAlive()) {				    	
			    	res = super.invoke("checkNumReplicas",null,nh, new Object[]{key,value});
			    	if (res==null) try {Thread.sleep(2000);} catch(Exception e) {}
			    }
			    if (res!=null) {
			      System.out.println("Replica stored in : "+nh);
			      replicators.put(nh,key);		
				  replicas.put(key, nh);
			      num++;
			    }  
			  }			    
			}	
	}
    
	/*********************************************************************/

	@RemoteUpdate
	public void replicaCheck(NodeHandle handle, boolean joined) {
		
		System.out.println("Replicator [replicaCheck]");
		
		if (joined) {	
			try {Thread.sleep(15000);} catch(Exception e) {}
			//check that replicators still are neigbours			
            for(NodeHandle nh : replicators.keySet()) {
				
				if (!nh.isAlive()) {
				  Set<String> keys = replicators.remove(nh);
				  if (keys!=null) {
				  for(String key : keys) {
					  replicas.remove(key,nh);
					  
				  }
				  }
				}  
			}					
		}
		else {
			 //delete dead replicator			
		     Set<String> keys = replicators.remove(handle);
		     if (keys!=null) {
		       for(String key : keys) replicas.remove(key,handle);
		     }  
        }
	    
		for(String key : replicas.keySet()) {
          checkNumReplicas(key,null);
		}
	}
	
	/**
	 * Forces Update (Crash Case)
	 */
	@RemotePointcut(id = "replicaPing", abstraction = Abstractions.DIRECT, synchro = true)
	@DamonPulse(seconds=11)		
	public void ping() {
		for (NodeHandle nh : replicators.keySet()) {
			super.invoke("ping",null,nh,new Object[]{});
		}
	}
	
	@RemoteAdvice(id = "replicaPing") 
	public void pong(RemoteJoinPoint rjp) {		
	  rjp.proceed(true);
	}
	
	    
}	