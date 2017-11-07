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
public class EuclideanVectorTest extends TestCase {

    public EuclideanVectorTest() {
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
     * Test of normalize method, of class EuclideanVector.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        EuclideanVector instance = null;
        // TODO review the generated test code and remove the default call to fail.

//        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class EuclideanVector.
     */
    @Test
    public void testGetLength() {
        System.out.println("getLength");
        EuclideanVector instance = null;
        double expResult = 0.0;
        double result = instance.getLength();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSquareLength method, of class EuclideanVector.
     */
    @Test
    public void testGetSquareLength() {
        System.out.println("getSquareLength");
        EuclideanVector instance = null;
        double expResult = 0.0;
        double result = instance.getSquareLength();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class EuclideanVector.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        EuclideanVector vector = null;
        EuclideanVector instance = null;
        instance.add(vector);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of minus method, of class EuclideanVector.
     */
    @Test
    public void testMinus() {
        System.out.println("minus");
        EuclideanVector vector = null;
        EuclideanVector instance = null;
        instance.minus(vector);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of scale method, of class EuclideanVector.
     */
    @Test
    public void testScale() {
        System.out.println("scale");
        double scale = 0.0;
        EuclideanVector instance = null;
        instance.scale(scale);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dotProductNormalized method, of class EuclideanVector.
     */
    @Test
    public void testDotProductNormalized() {
        System.out.println("dotProductNormalized");
        EuclideanVector vector = null;
        EuclideanVector instance = null;
        double expResult = 0.0;
        double result = instance.dotProductNormalized(vector);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fromVertex method, of class EuclideanVector.
     */
    @Test
    public void testFromVertex() {
        System.out.println("fromVertex");
        Vertex vertex = null;
        EuclideanVector expResult = null;
        EuclideanVector result = EuclideanVector.fromVertex(vertex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalizeZ method, of class EuclideanVector.
     */
    @Test
    public void testNormalizeZ() {
        System.out.println("normalizeZ");
        EuclideanVector instance = null;
        instance.normalizeZ();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
