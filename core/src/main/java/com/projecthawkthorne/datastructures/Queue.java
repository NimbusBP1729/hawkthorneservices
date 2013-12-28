/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projecthawkthorne.datastructures;

import java.util.LinkedList;

/**
 *
 * @author Patrick
 */
public class Queue<T> {
    LinkedList<T> q = new LinkedList<T>();

    public void push(T jump) {
        q.push(jump);
    }
    public T pop(){
        return q.removeLast();
    }

    public boolean flush() {
        if (q.isEmpty()){return false;}
        while(!q.isEmpty()){
            q.remove();
        }
        return true;
    }
}
