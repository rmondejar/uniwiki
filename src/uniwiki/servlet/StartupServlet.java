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
package uniwiki.servlet;

import java.io.File;

import javax.servlet.http.*;
import javax.servlet.*;

import uniwiki.concerns.DamonControl;

/**
 * @author Ruben Mondejar <ruben.mondejar@urv.cat>
 */
public class StartupServlet extends HttpServlet {
	
  public void init (ServletConfig config) throws ServletException {
    super.init (config);
    ServletContext ctx = config.getServletContext();

    try {        
        String xmlConfigFile = ctx.getRealPath (File.separator) + File.separator + "META-INF" + File.separator + "damon-config.xml";
        
        DamonControl.init(xmlConfigFile);		
                        
    } catch (Exception e) {
		
      System.out.println ("------------------------------ EXCEPTION --------------------------------");
      System.out.println ("-------------------------- Initializing Uniwiki ----------------------------");
      System.out.println ("-------------------------------------------------------------------------");
      e.printStackTrace();
      throw new ServletException (e.getMessage());
    }
  }
  
  public void destroy() {
        super.destroy();       
        DamonControl.close();  
    
  }
}
