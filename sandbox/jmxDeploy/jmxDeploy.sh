#
# The contents of this file are subject to the terms 
# of the Common Development and Distribution License 
# (the License).  You may not use this file except in
# compliance with the License.
# 
# You can obtain a copy of the license at 
# https://glassfish.dev.java.net/public/CDDLv1.0.html or
# glassfish/bootstrap/legal/CDDLv1.0.txt.
# See the License for the specific language governing 
# permissions and limitations under the License.
# 
# When distributing Covered Code, include this CDDL 
# Header Notice in each file and include the License file 
# at glassfish/bootstrap/legal/CDDLv1.0.txt.  
# If applicable, add the following below the CDDL Header, 
# with the fields enclosed by brackets [] replaced by
# you own identifying information: 
# "Portions Copyrighted [year] [name of copyright owner]"
# 
# Copyright 2006 Sun Microsystems, Inc. All rights reserved.
#
#!/bin/sh

AS_HOST=localhost
AS_PORT=port
AS_ADMINUSER=admin
AS_ADMINPASSWORD=adminadmin
AS_ADMINPORT=4848

$JAVA_HOME/bin/java -classpath ./target/classes:$S1AS_HOME/lib/appserv-deployment-client.jar:$S1AS_HOME/lib/jmxremote_optional.jar:. org.glassfish.deployment.util.JMXDeploy $1 $AS_HOST $2 $AS_ADMINUSER $AS_ADMINPASSWORD $AS_ADMINPORT



