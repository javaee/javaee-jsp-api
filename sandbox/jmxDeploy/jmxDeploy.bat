@rem
@rem The contents of this file are subject to the terms 
@rem of the Common Development and Distribution License 
@rem (the License).  You may not use this file except in
@rem compliance with the License.
@rem 
@rem You can obtain a copy of the license at 
@rem https://glassfish.dev.java.net/public/CDDLv1.0.html or
@rem glassfish/bootstrap/legal/CDDLv1.0.txt.
@rem See the License for the specific language governing 
@rem permissions and limitations under the License.
@rem 
@rem When distributing Covered Code, include this CDDL 
@rem Header Notice in each file and include the License file 
@rem at glassfish/bootstrap/legal/CDDLv1.0.txt.  
@rem If applicable, add the following below the CDDL Header, 
@rem with the fields enclosed by brackets [] replaced by
@rem you own identifying information: 
@rem "Portions Copyrighted [year] [name of copyright owner]"
@rem 
@rem Copyright 2006 Sun Microsystems, Inc. All rights reserved.
@rem

@set AS_HOST=localhost
@set AS_ADMINUSER=admin
@set AS_ADMINPASSWORD=adminadmin
@set AS_ADMINPORT=4848

%JAVA_HOME%/bin/java -classpath target/classes;%S1AS_HOME%/lib/appserv-deployment-client.jar;%S1AS_HOME%/lib/jmxremote_optional.jar org.glassfish.deployment.util.JMXDeploy %1 %AS_HOST% %2 %AS_ADMINUSER% %AS_ADMINPASSWORD% %AS_ADMINPORT%



