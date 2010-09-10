package com.dteoh.treasuremap;

import java.util.Locale;

import junit.framework.TestCase;

import org.jdesktop.application.ResourceMap;

/**
 * Tests for the resource map builder.
 * 
 * @author Douglas Teoh
 * 
 */
public class ResourceMapsTest extends TestCase {

    // Blank class for testing purposes.
    static class Another {
    }

    static class Parent {
    }

    /**
     * Test creating builder with null class.
     */
    public void testCtor1() {
        try {
            new ResourceMaps(null);
            fail("Expecting NPE.");
        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test creating builder with null class and locale.
     */
    public void testCtor2() {
        try {
            new ResourceMaps(null, null);
            fail("Expecting NPE.");
        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test creating builder with valid class and null locale.
     */
    public void testCtor3() {
        try {
            new ResourceMaps(getClass(), null);
            fail("Expecting NPE.");
        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test creating builder with null class and valid locale.
     */
    public void testCtor4() {
        try {
            new ResourceMaps(null, Locale.getDefault());
            fail("Expecting NPE.");
        } catch (NullPointerException e) {
            // OK.
        }
    }

    /**
     * Test creating a resource map using the default bundle.
     */
    public void testBuild1() {
        ResourceMap rMap = new ResourceMaps(getClass()).build();
        assertEquals("Hello", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
    }

    /**
     * Test creating a resource map using the "yy" locale's bundle.
     */
    public void testBuild2() {
        ResourceMap rMap = new ResourceMaps(getClass(), new Locale("yy"))
                .build();
        assertEquals("YY", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
    }

    /**
     * Test creating a resource map using the "zz" locale's bundle. There is no
     * associated properties file for zz.
     */
    public void testBuild3() {
        ResourceMap rMap = new ResourceMaps(getClass(), new Locale("zz"))
                .build();
        assertEquals("Hello", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
    }

    /**
     * Test creating a resource map with another class.
     */
    public void testAndBuild1() {
        ResourceMap rMap = new ResourceMaps(getClass()).and(Another.class)
                .build();
        assertEquals("Hello", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
        assertEquals("Another.txt", rMap.getString("File"));
    }

    /**
     * Test creating a resource map with another class and the "yy" locale.
     */
    public void testAndBuild2() {
        ResourceMap rMap = new ResourceMaps(getClass(), new Locale("yy")).and(
                Another.class).build();
        assertEquals("YY", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
        assertEquals("YY.txt", rMap.getString("File"));
    }

    /**
     * Test creating a resource map with another class and the "zz" locale.
     * There are no "zz" locale property bundles.
     */
    public void testAndBuild3() {
        ResourceMap rMap = new ResourceMaps(getClass(), new Locale("zz")).and(
                Another.class).build();
        assertEquals("Hello", rMap.getString("Greeting"));
        assertEquals("abc", rMap.getString("Message"));
        assertEquals("Another.txt", rMap.getString("File"));
    }

    /**
     * Test creating a resource map with a parent bundle.
     */
    public void testWithParentBuild1() {
        ResourceMap parentMap = new ResourceMaps(Parent.class).build();
        assertEquals("Parent", parentMap.getString("Who"));

        ResourceMap rMap = new ResourceMaps(getClass()).withParent(parentMap)
                .build();
        assertEquals("Parent", rMap.getString("Who"));
    }

}
