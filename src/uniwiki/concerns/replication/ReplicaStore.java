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

import integration.Patch;
import integration.PoolImplMock;
import integration.WootCoreI;
import integration.WootEngine;
import integration.WootPageStoreMock;

import java.util.Set;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

import damon.annotation.*;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;
import damon.util.Utilities;
import damon.util.collections.SetHashtable;

/**
 * Distribute Meta-Aspect ReplicaStore
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect (abstraction = Abstractions.LOCAL, target = "p2p://uniwiki")
public class ReplicaStore extends DistributedMetaAspect {
			
	    private static WootCoreI replicaBack;
	    private static SetHashtable<NodeHandle,String> owners; 
	    
	    public ReplicaStore() throws Exception {
	    	super();
	    	replicaBack = new WootEngine(new WootPageStoreMock(),new PoolImplMock());
	    	owners = new SetHashtable<NodeHandle,String>();
	    	
	    }

		public static synchronized WootCoreI getReplicaBack() {
			return replicaBack;
		}

		
		public static synchronized void putReplica(Patch patch) throws Exception {
			replicaBack.deliverPatch(patch);
		}

		public static synchronized Object getReplica(String pageName) throws Exception {
			return replicaBack.getWootPage(pageName);
		}
		
			
		@RemoteAdvice(id = "replicate")    
		public void replicaArrive(RemoteJoinPoint rjp, String key, Object value) throws Exception {
			System.out.println("ReplicaStore [replicate] : "+key);
			if (value instanceof Patch) {
			  putReplica((Patch)value);
			  owners.put(rjp.getOriginator(), key);			  
			} 
			rjp.proceed(key);
		}
	    
		
		@RemotePointcut(id = "put", abstraction = Abstractions.HOPPED, synchro = true)
		@RemoteUpdate
		public void ownerCheck(NodeHandle handle, boolean joined) throws Exception {
			
			System.out.println("ReplicaStore [ownerCheck] : "+owners.keySet());
			
			if (!joined && owners.containsKey(handle)) {
			
				  Set<String> keys = owners.remove(handle);
				  System.out.println("Detected dead owner : "+handle);
				  for(String key : keys) {
					  Object value = getReplica(key);
					  System.out.println("Re-insert id : "+key);
					  Object res = null; 
					  while(res==null) {
						  Id id =  Utilities.generateHash(key);
						  res = super.invoke("ownerCheck",null,id,new Object[]{key,value});
						  if (res==null) try {Thread.sleep(2000);} catch(Exception e) {}						    
					  }
				    
				  }  
			}
					
	    }
			
		/**
		 * Forces Update (Crash Case)
		 */
		@RemotePointcut(id = "ownerPing", abstraction = Abstractions.DIRECT, synchro = true)
		@DamonPulse(seconds=11)
		public void ping() {
			for (NodeHandle nh : owners.keySet()) {
				super.invoke("ping",null,nh,new Object[]{});
			}
		}
		
		@RemoteAdvice(id = "ownerPing") 
		public void pong(RemoteJoinPoint rjp) {		
		  rjp.proceed(true);
		}
}	