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

import org.codehaus.aspectwerkz.annotation.*;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;

import damon.reflection.thisEndPoint;

/**
 * Source Hook definition for Wiki
 * @author Rubén Mondéjar <ruben.mondejar@urv.cat>
 */
public class SourceHooks {
	
	@Before("call(* uniwiki.bean.WikiBean.save(..)) AND args(key,value)")   
	public void save(JoinPoint joinPoint, String key, String value) throws Throwable {
		//System.out.println("UniWikiHook : save("+key+","+value+")");
		thisEndPoint.addParams(joinPoint, key, value);
	}
	
	@Before("call(* uniwiki.bean.WikiBean.load(..)) AND args(key)")   
	public void load(JoinPoint joinPoint, String key) throws Throwable {
		//System.out.println("UniWikiHook : load ("+key+")");
		thisEndPoint.addParams(joinPoint, key);
	}
   
}	