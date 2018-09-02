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
package uniwiki.concerns.monitoring;

import rice.p2p.commonapi.Id;

import damon.annotation.*;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;

/**
 * Distributed Meta-Aspect Replicator
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect (abstraction = Abstractions.LOCAL, target = "p2p://uniwiki")
public class Monitor extends DistributedMetaAspect {	
	
	@RemoteMetaPointcut(id = "get", type = Type.BEFORE)	
	public void getLog(RemoteJoinPoint rjp, Id id, String key) {		
		System.out.println("Monitor [getLog] : ("+id+")");		
	}
	
	@RemoteMetaPointcut(id = "put", type = Type.BEFORE)    
	public void putLog(RemoteJoinPoint rjp, Id id, String key, Object value) {
		System.out.println("Monitor [putLog] : ("+id+","+value+")");		
	}        
}	