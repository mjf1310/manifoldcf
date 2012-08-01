/* $Id$ */

/**
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements. See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.manifoldcf.webcrawler_tests;

import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.*;
import java.util.*;

/** Mock wiki service */
public class MockWebService
{
  Server server;
  WebServlet servlet;
    
  public MockWebService(int docsPerLevel)
  {
    server = new Server(8191);
    server.setThreadPool(new QueuedThreadPool(100));
    servlet = new WebServlet(docsPerLevel);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/web");
    server.setHandler(context);
    context.addServlet(new ServletHolder(servlet), "/gen.php");
  }
    
  public void start() throws Exception
  {
    server.start();
  }
    
  public void stop() throws Exception
  {
    server.stop();
  }

  
  public static class WebServlet extends HttpServlet
  {
    int docsPerLevel;
    
    public WebServlet(int docsPerLevel)
    {
      this.docsPerLevel = docsPerLevel;
    }
    
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res)
      throws IOException
    {
      try {
      String resourceName = null;
      
      String site = req.getParameter("site");     // Site ID
      if (site == null)
        throw new IOException("Site ID parameter must be set");

      String level = req.getParameter("level");   // Level #
      if (site == null)
        throw new IOException("Level number parameter must be set");

      String item = req.getParameter("item");    // Item #
      if (item == null)
        throw new IOException("Item number parameter must be set");

      int theLevel;
      try
      {
        theLevel = Integer.parseInt(level);
      }
      catch (NumberFormatException e)
      {
        throw new IOException("Level number must be a number: "+level);
      }
      
      int theItem;
      try
      {
        theItem = Integer.parseInt(item);
      }
      catch (NumberFormatException e)
      {
        throw new IOException("Item number must be a number: "+item);
      }

      // Formulate the response.
      // First, calculate the number of docs on the current level
      int maxDocsThisLevel = 1;
      for (int i = 0 ; i < theLevel ; i++)
      {
        maxDocsThisLevel *= docsPerLevel;
      }
      if (theItem >= maxDocsThisLevel)
        // Not legal
        throw new IOException("Doc number too big: "+theItem+" ; level "+theLevel+" ; docsPerLevel "+docsPerLevel);

      // Generate the page
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/html; charset=utf-8");
      res.getWriter().printf("<html>\n");
      res.getWriter().printf("  <body>\n");

      res.getWriter().printf("This is doc number "+theItem+" and level number "+theLevel+" in site "+site+"\n");

      // Generate links to all parents
      int parentLevel = theLevel;
      int parentItem = theItem;
      while (parentLevel > 0)
      {
        parentLevel--;
        parentItem /= docsPerLevel;
	generateLink(res,site,parentLevel,parentItem);
      }
      
      // Generate links to direct children
      for (int i = 0; i < docsPerLevel; i++)
      {
        int docNumber = i + theItem * docsPerLevel;
        generateLink(res,site,theLevel+1,docNumber);
      }
      
      // Generate some cross-links to other items' children
      for (int i = 0; i < maxDocsThisLevel; i++)
      {
        int docNumber = theItem + i * docsPerLevel;
        generateLink(res,site,theLevel+1,docNumber);
      }
      
      res.getWriter().printf("  </body>\n");
      res.getWriter().printf("</html>\n");
      res.getWriter().flush();
      }
      catch (IOException e)
      {
        e.printStackTrace();
        throw e;
      }
    }
    
    protected void generateLink(HttpServletResponse res, String site, int level, int item)
      throws IOException
    {
      res.getWriter().printf("    <a href=\"http://localhost:8191/web/gen.php?site="+site+"&level="+level+"&item="+item+"\"/>\n");
    }

  }
}
