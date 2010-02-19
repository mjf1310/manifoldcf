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
package org.apache.lcf.crawler.connectors.webcrawler;


/** Constants for the Webcrawler connector configuration.
*/
public class WebcrawlerConfig
{
  public static final String _rcsid = "@(#)$Id$";


  // Constants describing the configuration structure.  This structure describes the "how" of
  // fetching page data - e.g. bandwidth and fetch constraints, adherance to robots conventions,
  // etc.
  // For the throttling part of the connector, the scheduler handles average fetch-rate limits.
  // The per-connection configuration describes the maximum number of connections per some user-defined criteria, as
  // well as bandwidth maximums and fetch rate absolute maximums.
  //
  // In detail:
  //
  // 1) The robots conventions;
  // 2) Bandwidth limits in KB/sec, based on regular expressions done on the bins;
  // 3) Email address (so people can whine to somebody about our crawler);
  // 4) Maximum number of connections per host, based on a regular expression done on the bins a document belongs to.
  // 5) Authentication information (NTLM and basic auth only), based on regexp of a document's URL.
  // 6) SSL trust store certificates, trusted on the basis of a regexp of a document's URL.

  /** Robots usage (a parameter) */
  public static final String PARAMETER_ROBOTSUSAGE = "Robots usage";
  /** Email (a parameter) */
  public static final String PARAMETER_EMAIL = "Email address";
  /** The bin description node */
  public static final String NODE_BINDESC = "bindesc";
  /** The bin regular expression */
  public static final String ATTR_BINREGEXP = "binregexp";
  /** Whether the match is case insensitive */
  public static final String ATTR_INSENSITIVE = "caseinsensitive";
  /** The max connections node */
  public static final String NODE_MAXCONNECTIONS = "maxconnections";
  /** The bandwidth node */
  public static final String NODE_MAXKBPERSECOND = "maxkbpersecond";
  /** The max fetch rate node */
  public static final String NODE_MAXFETCHESPERMINUTE = "maxfetchesperminute";
  /** The value attribute (used for maxconnections and maxkbpersecond) */
  public static final String ATTR_VALUE = "value";
  /** Access control description node */
  public static final String NODE_ACCESSCREDENTIAL = "accesscredential";
  /** Regexp for access control node */
  public static final String ATTR_URLREGEXP = "urlregexp";
  /** Type of security  */
  public static final String ATTR_TYPE = "type";
  /** Type value for basic authentication */
  public static final String ATTRVALUE_BASIC = "basic";
  /** Type value for NTLM authentication */
  public static final String ATTRVALUE_NTLM = "ntlm";
  /** Type value for session-based authentication */
  public static final String ATTRVALUE_SESSION = "session";
  /** Domain/realm part of credentials (if any) */
  public static final String ATTR_DOMAIN = "domain";
  /** Username part of credentials */
  public static final String ATTR_USERNAME = "username";
  /** Password part of credentials */
  public static final String ATTR_PASSWORD = "password";
  /** Authentication page description node */
  public static final String NODE_AUTHPAGE = "authpage";
  /** Authentication page type: Form */
  public static final String ATTRVALUE_FORM = "form";
  /** Authentication page type: Link */
  public static final String ATTRVALUE_LINK = "link";
  /** Authentication page type: Redirection */
  public static final String ATTRVALUE_REDIRECTION = "redirection";
  /** Form name or link target regexp for authentication page */
  public static final String ATTR_MATCHREGEXP = "match";
  /** Authentication parameter node */
  public static final String NODE_AUTHPARAMETER = "authparameter";
  /** Authentication parameter name regexp */
  public static final String ATTR_NAMEREGEXP = "name";
  /** Trust store description node */
  public static final String NODE_TRUST = "trust";
  /** Trust store section of authentication record */
  public static final String ATTR_TRUSTSTORE = "truststore";
  /** "Trust everything" attribute - replacing truststore if set to 'true' */
  public static final String ATTR_TRUSTEVERYTHING = "trusteverything";

  // Constants used in the document specification part of the configuration structure.
  // This describes the "what" of the job.

  /** The seeds node.  The value of this node contains the seeds, as a large
  * text area. */
  public static final String NODE_SEEDS = "seeds";
  /** Include regexps node.  The value of this node contains the regexps that
  * must match the canonical URL in order for that URL to be included.  These
  * regexps are newline separated, and # starts a comment.  */
  public static final String NODE_INCLUDES = "includes";
  /** Exclude regexps node.  The value of this node contains the regexps that
  * if any one matches, causes the URL to be excluded.  These
  * regexps are newline separated, and # starts a comment.  */
  public static final String NODE_EXCLUDES = "excludes";

}


