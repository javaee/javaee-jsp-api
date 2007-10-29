

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
package org.apache.tomcat.util.collections;

import java.util.Hashtable;

/**
 * This class implements a Generic LRU Cache
 *
 *
 * @author Ignacio J. Ortega
 *
 */

public class LRUCache
{
    class CacheNode
    {

        CacheNode prev;
        CacheNode next;
        Object value;
        Object key;

        CacheNode()
        {
        }
    }


    public LRUCache(int i)
    {
        currentSize = 0;
        cacheSize = i;
        nodes = new Hashtable(i);
    }

    public Object get(Object key)
    {
        CacheNode node = (CacheNode)nodes.get(key);
        if(node != null)
        {
            moveToHead(node);
            return node.value;
        }
        else
        {
            return null;
        }
    }

    public void put(Object key, Object value)
    {
        CacheNode node = (CacheNode)nodes.get(key);
        if(node == null)
        {
            if(currentSize >= cacheSize)
            {
                if(last != null)
                    nodes.remove(last.key);
                removeLast();
            }
            else
            {
                currentSize++;
            }
            node = new CacheNode();
        }
        node.value = value;
        node.key = key;
        moveToHead(node);
        nodes.put(key, node);
    }

    public Object remove(Object key) {
        CacheNode node = (CacheNode)nodes.get(key);
        if (node != null) {
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }
            if (last == node)
                last = node.prev;
            if (first == node)
                first = node.next;
        }
        return node;
    }

    public void clear()
    {
        first = null;
        last = null;
    }

    private void removeLast()
    {
        if(last != null)
        {
            if(last.prev != null)
                last.prev.next = null;
            else
                first = null;
            last = last.prev;
        }
    }

    private void moveToHead(CacheNode node)
    {
        if(node == first)
            return;
        if(node.prev != null)
            node.prev.next = node.next;
        if(node.next != null)
            node.next.prev = node.prev;
        if(last == node)
            last = node.prev;
        if(first != null)
        {
            node.next = first;
            first.prev = node;
        }
        first = node;
        node.prev = null;
        if(last == null)
            last = first;
    }

    private int cacheSize;
    private Hashtable nodes;
    private int currentSize;
    private CacheNode first;
    private CacheNode last;
}
