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
 */

package org.apache.catalina.deploy;

public final class Test {

    public static void main(String args[]) {

        String list[] = null;

        System.out.println("Creating new collection");
        SecurityCollection collection = new SecurityCollection();

        System.out.println("Adding GET and POST methods");
        collection.addMethod("GET");
        collection.addMethod("POST");

        System.out.println("Currently defined methods:");
        list = collection.findMethods();
        for (int i = 0; i < list.length; i++)
            System.out.println(" " + list[i]);
        System.out.println("Is DELETE included? " +
                           collection.findMethod("DELETE"));
        System.out.println("Is POST included? " +
                           collection.findMethod("POST"));

        System.out.println("Removing POST method");
        collection.removeMethod("POST");

        System.out.println("Currently defined methods:");
        list = collection.findMethods();
        for (int i = 0; i < list.length; i++)
            System.out.println(" " + list[i]);
        System.out.println("Is DELETE included? " +
                           collection.findMethod("DELETE"));
        System.out.println("Is POST included? " +
                           collection.findMethod("POST"));

    }

}
