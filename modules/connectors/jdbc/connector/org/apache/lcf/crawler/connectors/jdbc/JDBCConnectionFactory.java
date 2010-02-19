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
package org.apache.lcf.crawler.connectors.jdbc;

import org.apache.lcf.core.interfaces.*;
import org.apache.lcf.agents.interfaces.*;
import org.apache.lcf.crawler.system.Logging;
import org.apache.lcf.crawler.system.LCF;

import java.util.*;
import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;

import com.bitmechanic.sql.*;

/** This class creates a connection
*/
public class JDBCConnectionFactory
{
  public static final String _rcsid = "@(#)$Id$";

  private static Map driverMap;

  private static ConnectionPoolManager _pool = null;

  static
  {
    driverMap = new HashMap();
    driverMap.put("oracle:thin:@","oracle.jdbc.OracleDriver");
    driverMap.put("postgresql:","org.postgresql.Driver");
    driverMap.put("mysql:","org.gjt.mm.mysql.Driver");
    driverMap.put("jtds:sqlserver:","net.sourceforge.jtds.jdbc.Driver");
    driverMap.put("jtds:sybase:","net.sourceforge.jtds.jdbc.Driver");
    try
    {
      _pool = new ConnectionPoolManager(120);
    }
    catch (Exception e)
    {
      System.err.println("Can't set up pool");
      e.printStackTrace(System.err);
    }
  }

  private JDBCConnectionFactory()
  {
  }


  public static Connection getConnection(String providerName, String host, String database, String userName, String password)
    throws LCFException, ServiceInterruption
  {
    if (database.length() == 0)
      database = "_root_";

    String driverClassName = (String)driverMap.get(providerName);
    if (driverClassName == null)
      throw new LCFException("Unrecognized jdbc provider: '"+providerName+"'");

    String instanceName = null;
    // Special for MSSQL: Allow database spec to contain an instance name too, in form:
    // <instance>/<database>
    if (providerName.startsWith("jtds:"))
    {
      int slashIndex = database.indexOf("/");
      if (slashIndex != -1)
      {
        instanceName = database.substring(0,slashIndex);
        database = database.substring(slashIndex+1);
      }
    }

    String dburl = "jdbc:" + providerName + "//" + host + "/" + database + ((instanceName==null)?"":";instance="+instanceName);
    if (Logging.connectors != null && Logging.connectors.isDebugEnabled())
      Logging.connectors.debug("JDBC: The connect string is '"+dburl+"'");
    try
    {
      // Hope for a connection now
      if (_pool != null)
      {
        // Build a unique string to identify the pool.  This has to include
        // the database and host at a minimum.

        // Provider is part of the pool key, so that the pools can distinguish between different databases
        String poolKey = providerName + "/";

        // Distinguish between instance names and databases too
        if (instanceName == null)
          poolKey += host + "/" + database;
        else
          poolKey += host + "/" + instanceName + "/" + database;

        // Better include the credentials on the pool key, or we won't be able to change those and have it build new connections
        // The password value is SHA-1 hashed, because the pool driver reports the password in many exceptions and we don't want it
        // to be displayed.
        poolKey += "/" + userName + "/" + LCF.hash(password);

        synchronized (_pool)
        {
          ConnectionPool cp = null;
          try
          {
            cp = _pool.getPool(poolKey);
          }
          catch (Exception e)
          {
          }
          if (cp == null)
          {
            _pool.addAlias(poolKey, driverClassName, dburl,
              userName, password, 25, 300, 3600, 30, false);
            // cp = _pool.getPool(poolKey);
          }
        }
        return DriverManager.getConnection(
          ConnectionPoolManager.URL_PREFIX + poolKey, null, null);
      }
      else
        throw new LCFException("Can't get connection since pool driver did not initialize properly");
    }
    catch (java.sql.SQLException e)
    {
      // Unfortunately, the connection pool manager manages to eat all actual connection setup errors.  This makes it very hard to figure anything out
      // when something goes wrong.  So, we try again, going directly this time as a means of getting decent error feedback.
      try
      {
        if (userName != null && userName.length() > 0)
        {
          DriverManager.getConnection(dburl, userName, password).close();
        }
        else
        {
          DriverManager.getConnection(dburl).close();
        }
      }
      catch (java.sql.SQLException e2)
      {
        throw new LCFException("Error getting connection: "+e2.getMessage(),e2,LCFException.SETUP_ERROR);
      }
      // By definition, this must be a service interruption, because the direct route in setting up the connection succeeded.
      long currentTime = System.currentTimeMillis();
      throw new ServiceInterruption("Error getting connection: "+e.getMessage(),e,currentTime + 300000L,currentTime + 6 * 60 * 60000L,-1,true);
    }
    catch (java.lang.ClassNotFoundException e)
    {
      throw new LCFException("Driver class not found: "+e.getMessage(),e,LCFException.SETUP_ERROR);
    }
    catch (java.lang.InstantiationException e)
    {
      throw new LCFException("Driver class not instantiable: "+e.getMessage(),e,LCFException.SETUP_ERROR);
    }
    catch (java.lang.IllegalAccessException e)
    {
      throw new LCFException("Driver class not accessible: "+e.getMessage(),e,LCFException.SETUP_ERROR);
    }
  }

  public static void releaseConnection(Connection c)
    throws LCFException, ServiceInterruption
  {
    try
    {
      c.close();
    }
    catch (java.sql.SQLException e)
    {
      throw new LCFException("Error releasing connection: "+e.getMessage(),e);
    }
  }

}

