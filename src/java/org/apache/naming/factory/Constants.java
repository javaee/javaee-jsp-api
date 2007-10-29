

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Portions Copyright Apache Software Foundation.
 */ 


package org.apache.naming.factory;


/**
 * Static constants for this package.
 */

public final class Constants {

    public static final String Package = "org.apache.naming.factory";

    public static final String DEFAULT_RESOURCE_FACTORY = 
        Package + ".ResourceFactory";

    public static final String DEFAULT_RESOURCE_LINK_FACTORY = 
        Package + ".ResourceLinkFactory";

    public static final String DEFAULT_TRANSACTION_FACTORY = 
        Package + ".TransactionFactory";

    public static final String DEFAULT_RESOURCE_ENV_FACTORY = 
        Package + ".ResourceEnvFactory";

    public static final String DEFAULT_EJB_FACTORY = 
        Package + ".EjbFactory";

    public static final String DBCP_DATASOURCE_FACTORY = 
        "org.apache.commons.dbcp.BasicDataSourceFactory";

    public static final String OPENEJB_EJB_FACTORY = 
        Package + ".OpenEjbFactory";

    public static final String OBJECT_FACTORIES = "";

    public static final String FACTORY = "factory";

}
