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

import java.util.Hashtable;

import integration.Patch;
import integration.WootPage;

import damon.annotation.*;
import damon.invokation.RemoteJoinPoint;
import damon.metalevel.aspectwerkz.DistributedMetaAspect;
import editor.PersistentClockImpl;
import editor.WootEditor;
import editor.WootSession;

/**
 * Distributed Meta-Aspect Merge
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
@DistributedAspect (abstraction = Abstractions.LOCAL, target = "p2p://uniwiki")
public class Editor extends DistributedMetaAspect {
		
	private WootEditor front;
	private Hashtable<String,WootSession> sessions = new Hashtable<String,WootSession>();
	
	public Editor() throws Exception {
		int siteid = (int) Math.random()*Integer.MAX_VALUE;
		//int siteid = Integer.parseInt(thisEndPoint.getLocalNodeHandle().getId().toString()); 
		front = new WootEditor(new Integer(siteid),new PersistentClockImpl("clock"));
	}
	
	/**
	 * Save the session on edit
	 * @param rjp
	 * @param value
	 */
	@RemoteMetaPointcut(id = "get", type = Type.AFTER, ack = true)    
	public void edit(RemoteJoinPoint rjp, String key, Object value) {
		if (value instanceof WootPage) {
		  System.out.println("Editor [edit] : "+key); 
		  WootPage page = (WootPage) value;		  
		  WootSession session = sessions.get(key);
		  if (session==null) session = front.edit(page);		  
		  sessions.put(key, session);
		  rjp.setResult(page.toHumanString());
		}  
	}
	
	/**
	 * Recovers session data, and changes the value (content) for the Patch
	 * @param rjp
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	@RemoteMetaPointcut(id = "put", type = Type.BEFORE)    
	public void patch(RemoteJoinPoint rjp, String key, Object value) throws Exception {		
        System.out.println("Editor [patch] : "+key);    
        
        WootSession session = sessions.get(key);
        if (session==null) session = front.edit(new WootPage(key));
		session.setContent((String) value);
		Patch patch = session.save();		        
		rjp.getArgs()[1] = patch;		 
		sessions.remove(key);
	}    
}	