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
package uniwiki.concerns.consistency;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import integration.Pool;
import integration.WootCoreI;
import integration.WootEngine;
import integration.WootPageLogImplMock;
import integration.WootPageStoreMock;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import uniwiki.concerns.replication.ReplicaStore;
import uniwiki.concerns.replication.Replicator;

import damon.annotation.*;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;
import damon.util.Utilities;
import integration.Patch;
import integration.WootPage;

/**
 * Distributed Meta-Aspect Integrator
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect (abstraction = Abstractions.LOCAL, target = "p2p://uniwiki")
public class Integrator extends DistributedMetaAspect {
		
	private WootCoreI back;
	private int counter = 0;
	private final int MAX = 1000;
	
	
	public Integrator() throws Exception {		
		back = new WootEngine(new WootPageStoreMock(),new Pool("pool"));
	}
	
	/**
	 * Changes the current value (Patch) for a Page
	 * @param rjp
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	@RemoteMetaPointcut(id = "put", type = Type.AFTER)    
	public void merge(RemoteJoinPoint rjp, String key, Object value) throws Exception {
		Id id =  Utilities.generateHash(key);        
        if (value instanceof Patch) {
          System.out.println("Integrator [merge] : "+key);	
          Patch patch = (Patch) value;
          back.deliverPatch(patch);
          WootPage page = back.getWootPage(key);
          Object[] args = rjp.getArgs();
          args[1] = page;          
          rjp.setArgs(args);
		} 
        else {
          System.out.println("Integrator [NO merge] : "+id);	
        }
	}  
	
	
	@DamonPulse(seconds=11)	
	public void antiEntropy() throws Exception {
		
		if (counter>MAX) {
		  
		  //find replicate randomly
		  Set<NodeHandle> replicators = Replicator.getReplicaHosts();
		  int num = (int) Math.random()*replicators.size();
		  Iterator<NodeHandle> it = replicators.iterator();
		  NodeHandle nh = it.next();
		  for (int i=1;i<num;i++) {
			  nh = it.next();
		  }
		  //apply antientropy
		  antiEntropy(nh);
		}
		counter=(counter+1)%MAX;		
	}
	
	
	
	@RemoteInvocation(id = "recover", abstraction = Abstractions.DIRECT, synchro = true)
	public void antiEntropy(NodeHandle nh) throws Exception {
		
		String[] pages=back.listPages();
		for (String pageName : pages) {
			WootPage page=back.getWootPage(pageName);
			if (page==null) {
				page = new WootPage(pageName, new WootPageLogImplMock());
			}
			//Vector<Patch> vp=back.recover(pageName, page.getLog().list()); //remotely
			Vector<Patch> vp = (Vector<Patch>) super.invoke("antiEntropy",null,nh,new Object[]{pageName, page.getLog().list()});
			if (vp!=null) {
			  for (Patch p:vp) {
				back.deliverPatch(p);
			  }
			}  
		}
	}
	
	@RemoteMethod(id = "recover")
	public void remoteRecover(RemoteJoinPoint rjp, String pageName, Vector<String> logList) throws Exception {
					
		WootCoreI replicaBack = ReplicaStore.getReplicaBack();
		
		Vector<Patch> vp= replicaBack.recover(pageName, logList); 
		
		rjp.proceed(vp);
	}
	
    
}	