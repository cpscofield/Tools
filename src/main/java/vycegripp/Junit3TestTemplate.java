// =============================================================================
// Junit3TestTemplate by Cary Scofield (carys689@gmail.com) is licensed under 
// a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================


package vycegripp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.AssertionFailedError;
import junit.framework.Assert;

/**
 * This is JUnit 3.x template code for creating a suite of either unit test
 * methods or test suites. Use the testMethod() method as a template for a
 * unit test method. Construct the suite of test methods or test suites
 * by adding entries to TestSuite in the suite() method.
 */
public class Junit3TestTemplate extends TestCase {
    
    public Junit3TestTemplate(String testName) {
        super(testName);
    }
    
    static long startTime = 0;
    static long endTime = 0;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.out.println( "=======================================" );
        System.out.println( "Beginning Test: " + super.getName() );
        System.out.println( "- - - - - - - - - - - - - - - - - - - -" );
        startTime = System.currentTimeMillis();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        endTime = System.currentTimeMillis();
        System.out.println( "- - - - - - - - - - - - - - - - - - - -" );
        System.out.print( "Ended Test: " + super.getName() );
        System.out.println( "; Time: " + (endTime-startTime) + " milliseconds" );
        System.out.println( "=======================================\n" );
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTest( new Junit3TestTemplate( "testMethod" ) );
        suite.addTest( new Junit3TestTemplate( "testMethod2" ) );
        return suite;
    }

    /**
     * Test method
     */
    public void testMethod() throws Exception {
        try {
            /* Put test code here */
        } catch (AssertionFailedError e) {
            System.err.println(e.toString());
            Assert.fail(e.toString());
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
    
    /**
     * Test method 2
     */
    public void testMethod2() throws Exception {
        try {
            /* Put test code here */
        } catch (AssertionFailedError e) {
            System.err.println(e.toString());
            Assert.fail(e.toString());
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
 }
