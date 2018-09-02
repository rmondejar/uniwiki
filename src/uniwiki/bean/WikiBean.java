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
package uniwiki.bean;

import java.util.Collection;
import java.util.Hashtable;

/**
 * Simple Bean Class for Wiki Applications
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
public class WikiBean {
	
	private Hashtable<String,String> pages = new Hashtable<String,String>();
	
	public void save(String name, String page) {
		//System.out.println("Wiki save : "+name);
		pages.put(name,page);
	}
	
    public String load(String name) {
    	//System.out.println("Wiki load : "+name);
		return pages.get(name);
	}	
    
    public Collection<String> list() {
    	return pages.keySet();
    }
	
} 