/**
 * 
 */
package com.treetank.service.rest.helper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.service.rest.RestTestHelper;

public class HelperDeleteTest {

    private HelperDelete toTest;

    @Before
    public void setUp() {
        toTest = new HelperDelete(RestTestHelper.getTestInstances());
    }

    /**
     * Test method for
     * {@link com.treetank.service.rest.helper.HelperDelete#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     * .
     */
    @Test
    @Ignore
    public void testHandle() {
        System.out.println();
    }

    @After
    public void tearDown() {
        TestHelper.closeEverything();
    }

}
