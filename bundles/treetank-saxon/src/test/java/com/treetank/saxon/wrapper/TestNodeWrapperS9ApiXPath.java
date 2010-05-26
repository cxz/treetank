package com.treetank.saxon.wrapper;

import junit.framework.TestCase;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.access.Database;
import com.treetank.api.IDatabase;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.saxon.evaluator.XPathEvaluator;
import com.treetank.utils.DocumentCreater;

/**
 * Test XPath S9Api.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class TestNodeWrapperS9ApiXPath {

	/** Treetank session on Treetank test document. */
	private transient static ISession sessionTest;

	@BeforeClass
	public static void setUp() throws TreetankException {
		Database.truncateDatabase(TestHelper.PATHS.PATH1.getFile());
		final IDatabase database = Database.openDatabase(TestHelper.PATHS.PATH1
				.getFile());
		sessionTest = database.getSession();
		final IWriteTransaction wtx = sessionTest.beginWriteTransaction();
		DocumentCreater.create(wtx);
		wtx.commit();
		wtx.close();
	}

	@AfterClass
	public static void tearDown() throws TreetankException {
		Database.forceCloseDatabase(TestHelper.PATHS.PATH1.getFile());
	}

	@Test
	public void testB1() throws Exception {
		final XPathSelector selector = new XPathEvaluator("//b[1]",
				sessionTest, TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder strBuilder = new StringBuilder();

		for (final XdmItem item : selector) {
			strBuilder.append(item.toString());
		}

		TestCase.assertEquals("<b xmlns:p=\"ns\">foo<c xmlns:p=\"ns\"/>\n</b>",
				strBuilder.toString());
	}

	@Test
	public void testB1String() throws Exception {
		final XPathSelector selector = new XPathEvaluator("//b[1]/text()",
				sessionTest, TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder strBuilder = new StringBuilder();

		for (final XdmItem item : selector) {
			strBuilder.append(item.toString());
		}

		TestCase.assertEquals("foo", strBuilder.toString());
	}

	@Test
	public void testB2() throws Exception {
		final XPathSelector selector = new XPathEvaluator("//b[2]",
				sessionTest, TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder strBuilder = new StringBuilder();

		for (final XdmItem item : selector) {
			strBuilder.append(item.toString());
		}

		TestCase.assertEquals(
				"<b xmlns:p=\"ns\" p:x=\"y\">\n   <c xmlns:p=\"ns\"/>bar</b>",
				strBuilder.toString());
	}

	@Test
	public void testB2Text() throws Exception {
		final XPathSelector selector = new XPathEvaluator("//b[2]/text()",
				sessionTest, TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder strBuilder = new StringBuilder();

		for (final XdmItem item : selector) {
			strBuilder.append(item.toString());
		}

		TestCase.assertEquals("bar", strBuilder.toString());
	}

	@Test
	public void testB() throws Exception {
		final XPathSelector selector = new XPathEvaluator("//b", sessionTest,
				TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder strBuilder = new StringBuilder();

		for (final XdmItem item : selector) {
			strBuilder.append(item.toString());
		}

		TestCase
				.assertEquals(
						"<b xmlns:p=\"ns\">foo<c xmlns:p=\"ns\"/>\n</b><b xmlns:p=\"ns\" p:x=\"y\">\n   "
								+ "<c xmlns:p=\"ns\"/>bar</b>", strBuilder
								.toString());
	}

	@Test
	public void testCountB() throws Exception {
		final XPathSelector selector = new XPathEvaluator("count(//b)",
				sessionTest, TestHelper.PATHS.PATH1.getFile()).call();

		final StringBuilder sb = new StringBuilder();

		for (final XdmItem item : selector) {
			sb.append(item.getStringValue());
		}

		TestCase.assertEquals("2", sb.toString());
	}

}