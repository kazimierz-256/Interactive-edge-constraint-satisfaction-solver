/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gk1;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kazimierz
 */
public class VectorTest extends TestCase {

    public VectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of normalize method, of class Vector.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        Vector instance = null;
        // TODO review the generated test code and remove the default call to fail.

//        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class Vector.
     */
    @Test
    public void testGetLength() {
        System.out.println("getLength");
        Vector instance = null;
        double expResult = 0.0;
        double result = instance.getLength();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquareLength method, of class Vector.
     */
    @Test
    public void testGetSquareLength() {
        System.out.println("getSquareLength");
        Vector instance = null;
        double expResult = 0.0;
        double result = instance.getSquareLength();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class Vector.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Vector vector = null;
        Vector instance = null;
        instance.add(vector);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of minus method, of class Vector.
     */
    @Test
    public void testMinus() {
        System.out.println("minus");
        Vector vector = null;
        Vector instance = null;
        instance.minus(vector);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scale method, of class Vector.
     */
    @Test
    public void testScale() {
        System.out.println("scale");
        double scale = 0.0;
        Vector instance = null;
        instance.scale(scale);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dotProductNormalized method, of class Vector.
     */
    @Test
    public void testDotProductNormalized() {
        System.out.println("dotProductNormalized");
        Vector vector = null;
        Vector instance = null;
        double expResult = 0.0;
        double result = instance.dotProductNormalized(vector);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fromVertex method, of class Vector.
     */
    @Test
    public void testFromVertex() {
        System.out.println("fromVertex");
        Vertex vertex = null;
        Vector expResult = null;
        Vector result = Vector.fromVertex(vertex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalizeZ method, of class Vector.
     */
    @Test
    public void testNormalizeZ() {
        System.out.println("normalizeZ");
        Vector instance = null;
        instance.normalizeZ();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
