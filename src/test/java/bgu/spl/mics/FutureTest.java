package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {
    Future<String> futureObject;
    @org.junit.Before
    public void setUp() throws Exception {
        futureObject = new Future<String>();

    }
    @Test
    public void get() {
        String s=new String();
        assertTrue(futureObject.get().getClass().equals(s.getClass()));
    }

    @Test
    public void resolve() {
        Future<Integer> futureObject1=new Future<Integer>();
        futureObject1.resolve(new Integer(1));
        assertTrue(futureObject1.get().getClass().equals(new Integer(1)));
    }

    @Test
    public void isDone() {
        Future<Integer> futureObject1=new Future<Integer>();
        futureObject1.resolve(new Integer(1));
        boolean b=futureObject1.isDone();
        assertTrue(b);
    }

    @Test
    public void get1() {
        String s=new String();
        assertTrue(futureObject.get().getClass().equals(s.getClass()));
    }
}