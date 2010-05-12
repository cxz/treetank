package com.treetank.service.jaxrx.implementation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.ws.rs.core.StreamingOutput;

import org.jaxrx.core.JaxRxException;
import org.jaxrx.core.QueryParameter;

import com.treetank.access.Database;
import com.treetank.api.IDatabase;
import com.treetank.api.IReadTransaction;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.exception.TreetankException;
import com.treetank.service.jaxrx.enums.EIdAccessType;
import com.treetank.service.jaxrx.util.RESTProps;
import com.treetank.service.jaxrx.util.RestXPathProcessor;
import com.treetank.service.jaxrx.util.WorkerHelper;
import com.treetank.service.xml.XMLSerializer;

/**
 * This class is responsible to work with database specific XML node id's. It
 * allows to access a resource by a node id, modify an existing resource by node
 * id, delete an existing resource by node id and to append a new resource to an
 * existing XML element identified by a node id.
 * 
 * @author Patrick Lang, Lukas Lewandowski, University of Konstanz
 * 
 */
public class NodeIdRepresentation {

	/**
	 * The folder where the tnk files will be saved.
	 */
	private static final transient String RESPATH = RESTProps.STOREDBPATH;
	/**
	 * The tnk file ending.
	 */
	private static final transient String TNKEND = ".tnk";

	/**
	 * This field specifies the begin result element of the request.
	 */
	private static final transient byte[] BEGINRESULT = "<jaxrx:result xmlns:jaxrx=\"http://jaxrx.org/\">"
			.getBytes();

	/**
	 * This field specifies the end result element of the request.
	 */
	private static final transient byte[] ENDRESULT = "</jaxrx:result>"
			.getBytes();

	private static final transient String NOTFOUND = "Node id not found";

	/**
	 * The 'yes' string.
	 */
	private static final transient String YESSTRING = "yes";

	/**
	 * This method is responsible to deliver the whole XML resource addressed by
	 * a unique node id.
	 * 
	 * @param resourceName
	 *            The name of the database, where the node id belongs.
	 * @param nodeId
	 *            The unique node id of the requested resource.
	 * @param queryParams
	 *            The optional query parameters.
	 * @return The whole XML resource addressed by a unique node id.
	 * @throws JaxRxException
	 *             The exception occurred.
	 */
	public StreamingOutput getResource(final String resourceName,
			final long nodeId, final Map<QueryParameter, String> queryParams)
			throws JaxRxException {
		final StreamingOutput sOutput = new StreamingOutput() {
			@Override
			public void write(final OutputStream output) throws IOException,
					JaxRxException {

				final String tnkFile = RESPATH + File.separatorChar
						+ resourceName + TNKEND;
				final File dbFile = new File(tnkFile);

				// final String xPath = queryParams.get(QueryParameter.QUERY);
				final String revision = queryParams
						.get(QueryParameter.REVISION);
				final String wrap = queryParams.get(QueryParameter.WRAP);
				final String doNodeId = queryParams.get(QueryParameter.OUTPUT);
				final boolean wrapResult = (wrap == null) ? false : wrap
						.equalsIgnoreCase(YESSTRING);
				final boolean nodeid = (doNodeId == null) ? false : doNodeId
						.equalsIgnoreCase(YESSTRING);
				final Long rev = revision == null ? null : Long
						.valueOf(revision);
				serialize(dbFile, nodeId, rev, nodeid, output, wrapResult);
			}
		};

		return sOutput;
	}

	/**
	 * This method is responsible to deliver the whole XML resource addressed by
	 * a unique node id.
	 * 
	 * @param resourceName
	 *            The name of the database, where the node id belongs.
	 * @param nodeId
	 *            The unique node id of the requested resource.
	 * @param queryParams
	 *            The optional query parameters.
	 * @param accessType
	 *            The id access type to access a resource by a relative method
	 *            type defined in {@link EIdAccessType}.
	 * @return The whole XML resource addressed by a unique node id.
	 * @throws JaxRxException
	 *             The exception occurred.
	 */
	public StreamingOutput getResourceByAT(final String resourceName,
			final long nodeId, final Map<QueryParameter, String> queryParams,
			final EIdAccessType accessType) throws JaxRxException {
		final StreamingOutput sOutput = new StreamingOutput() {
			@Override
			public void write(final OutputStream output) throws IOException,
					JaxRxException {

				final String tnkFile = RESPATH + File.separatorChar
						+ resourceName + TNKEND;
				final File dbFile = new File(tnkFile);

				// final String xPath = queryParams.get(QueryParameter.QUERY);
				final String revision = queryParams
						.get(QueryParameter.REVISION);
				final String wrap = queryParams.get(QueryParameter.WRAP);
				final String doNodeId = queryParams.get(QueryParameter.OUTPUT);
				final boolean wrapResult = (wrap == null) ? false : wrap
						.equalsIgnoreCase(YESSTRING);
				final boolean nodeid = (doNodeId == null) ? false : doNodeId
						.equalsIgnoreCase(YESSTRING);
				final Long rev = revision == null ? null : Long
						.valueOf(revision);
				serializeAT(dbFile, nodeId, rev, nodeid, output, wrapResult,
						accessType);
			}
		};

		return sOutput;
	}

	/**
	 * This method is responsible to perform a XPath query expression on the XML
	 * resource which is addressed through a unique node id.
	 * 
	 * @param resourceName
	 *            The name of the database, the node id belongs to.
	 * @param nodeId
	 *            The node id of the requested resource.
	 * @param query
	 *            The XPath expression.
	 * @param queryParams
	 *            The optional query parameters (output, wrap, revision).
	 * @return The result of the XPath query expression.
	 */
	public StreamingOutput performQueryOnResource(final String resourceName,
			final long nodeId, final String query,
			final Map<QueryParameter, String> queryParams) {

		final StreamingOutput sOutput = new StreamingOutput() {
			@Override
			public void write(final OutputStream output) throws IOException,
					JaxRxException {

				final String tnkFile = RESPATH + File.separatorChar
						+ resourceName + TNKEND;
				final File dbFile = new File(tnkFile);
				final String revision = queryParams
						.get(QueryParameter.REVISION);
				final String wrap = queryParams.get(QueryParameter.WRAP);
				final String doNodeId = queryParams.get(QueryParameter.OUTPUT);
				final boolean wrapResult = (wrap == null) ? true : wrap
						.equalsIgnoreCase(YESSTRING);
				final boolean nodeid = (doNodeId == null) ? false : doNodeId
						.equalsIgnoreCase(YESSTRING);
				final Long rev = revision == null ? null : Long
						.valueOf(revision);
				final RestXPathProcessor xpathProcessor = new RestXPathProcessor();
				try {
					xpathProcessor.getXpathResource(dbFile, nodeId, query,
							nodeid, rev, output, wrapResult);
				} catch (final TreetankException exce) {
					throw new JaxRxException(exce);
				}
			}
		};

		return sOutput;
	}

	/**
	 * This method is responsible to delete an XML resource addressed through a
	 * unique node id (except root node id).
	 * 
	 * @param resourceName
	 *            The name of the database, which the node id belongs to.
	 * @param nodeId
	 *            The unique node id.
	 * @throws JaxRxException
	 *             The exception occurred.
	 */
	public void deleteResource(final String resourceName, final long nodeId)
			throws JaxRxException {
		synchronized (resourceName) {
			ISession session = null;
			IDatabase database = null;
			IWriteTransaction wtx = null;
			final String tnkFile = RESPATH + File.separatorChar + resourceName
					+ TNKEND;
			final File dbFile = new File(tnkFile);
			boolean abort = false;
			if (WorkerHelper.checkExistingResource(dbFile)) {
				try {
					database = Database.openDatabase(dbFile);
					// Creating a new session
					session = database.getSession();
					// Creating a write transaction
					wtx = session.beginWriteTransaction();
					// move to node with given rest id and deletes it
					if (wtx.moveTo(nodeId)) {
						wtx.remove();
						wtx.commit();
					} else {
						// workerHelper.closeWTX(abort, wtx, session, database);
						throw new JaxRxException(404, NOTFOUND);
					}
				} catch (final TreetankException exce) {
					abort = true;
					throw new JaxRxException(exce);
				} finally {
					try {
						WorkerHelper.closeWTX(abort, wtx, session, database);
					} catch (final TreetankException exce) {
						throw new JaxRxException(exce);
					}
				}
			} else {
				throw new JaxRxException(404, "DB not found");
			}
		}
	}

	/**
	 * This method is responsible to modify the XML resource, which is addressed
	 * through a unique node id.
	 * 
	 * @param resourceName
	 *            The name of the database, where the node id belongs to.
	 * @param nodeId
	 *            The node id.
	 * @param newValue
	 *            The new value of the node that has to be replaced.
	 * @throws JaxRxException
	 *             The exception occurred.
	 */
	public void modifyResource(final String resourceName, final long nodeId,
			final InputStream newValue) throws JaxRxException {
		synchronized (resourceName) {
			ISession session = null;
			IDatabase database = null;
			IWriteTransaction wtx = null;
			final String tnkFile = RESPATH + File.separatorChar + resourceName
					+ TNKEND;
			final File dbFile = new File(tnkFile);
			boolean abort = false;
			if (WorkerHelper.checkExistingResource(dbFile)) {
				try {
					database = Database.openDatabase(dbFile);
					// Creating a new session
					session = database.getSession();
					// Creating a write transaction
					wtx = session.beginWriteTransaction();

					if (wtx.moveTo(nodeId)) {
						final long parentKey = wtx.getNode().getParentKey();
						wtx.remove();
						wtx.moveTo(parentKey);
						WorkerHelper.shredInputStream(wtx, newValue, true);

					} else {
						// workerHelper.closeWTX(abort, wtx, session, database);
						throw new JaxRxException(404, NOTFOUND);
					}

				} catch (final TreetankException exc) {
					abort = true;
					throw new JaxRxException(exc);
				} finally {
					try {
						WorkerHelper.closeWTX(abort, wtx, session, database);
					} catch (final TreetankException exce) {
						throw new JaxRxException(exce);
					}
				}
			} else {
				throw new JaxRxException(404, "Requested resource not found");
			}
		}
	}

	/**
	 * This method is responsible to perform a POST request to a node id. This
	 * method adds a new XML subtree as first child or as right sibling to the
	 * node which is addressed through a node id.
	 * 
	 * @param resourceName
	 *            The name of the database, the node id belongs to.
	 * @param nodeId
	 *            The node id.
	 * @param input
	 *            The new XML subtree.
	 * @param type
	 *            The type which indicates if the new subtree has to be inserted
	 *            as right sibling or as first child.
	 * @throws JaxRxException
	 *             The exception occurred.
	 */
	public void addSubResource(final String resourceName, final long nodeId,
			final InputStream input, final EIdAccessType type)
			throws JaxRxException {
		ISession session = null;
		IDatabase database = null;
		IWriteTransaction wtx = null;
		final String tnkFile = RESPATH + File.separatorChar + resourceName
				+ TNKEND;
		final File dbFile = new File(tnkFile);
		synchronized (resourceName) {
			boolean abort;
			if (WorkerHelper.checkExistingResource(dbFile)) {
				abort = false;
				try {

					database = Database.openDatabase(dbFile);
					// Creating a new session
					session = database.getSession();
					// Creating a write transaction
					wtx = session.beginWriteTransaction();
					final boolean exist = wtx.moveTo(nodeId);
					if (exist) {
						if (type == EIdAccessType.FIRSTCHILD) {
							WorkerHelper.shredInputStream(wtx, input, true);
						} else if (type == EIdAccessType.RIGHTSIBLING) {
							WorkerHelper.shredInputStream(wtx, input, false);
						} else if (type == EIdAccessType.LASTCHILD) {
							if (wtx.moveToFirstChild()) {
								long last = wtx.getNode().getNodeKey();
								while (wtx.moveToRightSibling()) {
									last = wtx.getNode().getNodeKey();
								}
								wtx.moveTo(last);
								WorkerHelper
										.shredInputStream(wtx, input, false);

							} else {
								throw new JaxRxException(404, NOTFOUND);
							}

						} else if (type == EIdAccessType.LEFTSIBLING
								&& wtx.moveToLeftSibling()) {

							WorkerHelper.shredInputStream(wtx, input, false);

						}
					} else {
						throw new JaxRxException(404, NOTFOUND);
					}
				} catch (final JaxRxException exce) { // NOPMD due
					// to
					// different
					// exception
					// types
					abort = true;
					throw exce;
				} catch (final Exception exce) {
					abort = true;
					throw new JaxRxException(exce);
				} finally {
					try {
						WorkerHelper.closeWTX(abort, wtx, session, database);
					} catch (final TreetankException exce) {
						throw new JaxRxException(exce);
					}
				}
			}
		}
	}

	/**
	 * This method serializes requested resource
	 * 
	 * @param dbFile
	 *            The requested XML resource as tnk file.
	 * @param nodeId
	 *            The node id of the requested resource.
	 * @param revision
	 *            The revision of the requested resource.
	 * @param doNodeId
	 *            Specifies whether the node id's have to be shown in the
	 *            result.
	 * @param output
	 *            The output stream to be written.
	 * @param wrapResult
	 *            Specifies whether the result has to be wrapped with a result
	 *            element.
	 */
	private void serialize(final File dbFile, final long nodeId,
			final Long revision, final boolean doNodeId,
			final OutputStream output, final boolean wrapResult) {
		if (WorkerHelper.checkExistingResource(dbFile)) {
			ISession session = null;
			IDatabase database = null;
			IReadTransaction rtx = null;
			try {
				database = Database.openDatabase(dbFile);
				session = database.getSession();
				if (revision == null) {
					rtx = session.beginReadTransaction();
				} else {
					rtx = session.beginReadTransaction(revision);
				}

				// move to node with given id and read it
				if (wrapResult) {
					output.write(BEGINRESULT);
					if (rtx.moveTo(nodeId)) {
						new XMLSerializer(rtx, output, false, doNodeId).call();
					} else {
						// workerHelper.close(null, rtx, session, database);
						throw new JaxRxException(404, NOTFOUND);
					}
					output.write(ENDRESULT);
				} else {
					if (rtx.moveTo(nodeId)) {
						new XMLSerializer(rtx, output, false, doNodeId).call();
					} else {
						// workerHelper.close(null, rtx, session, database);
						throw new JaxRxException(404, NOTFOUND);
					}

				}
			} catch (final TreetankException ttExcep) {
				throw new JaxRxException(ttExcep);
			} catch (final IOException ioExcep) {
				throw new JaxRxException(ioExcep);
			} catch (final Exception globExcep) {
				if (globExcep instanceof JaxRxException) { // NOPMD due
					// to
					// different
					// exception
					// types
					throw (JaxRxException) globExcep;
				} else {
					throw new JaxRxException(globExcep);
				}
			} finally {
				try {
					WorkerHelper.closeRTX(rtx, session, database);
				} catch (final TreetankException exce) {
					throw new JaxRxException(exce);
				}
			}

		} else {
			throw new JaxRxException(404, "Resource does not exist");
		}

	}

	/**
	 * This method serializes requested resource with an access type.
	 * 
	 * @param dbFile
	 *            The requested XML resource as tnk file.
	 * @param nodeId
	 *            The node id of the requested resource.
	 * @param revision
	 *            The revision of the requested resource.
	 * @param doNodeId
	 *            Specifies whether the node id's have to be shown in the
	 *            result.
	 * @param output
	 *            The output stream to be written.
	 * @param wrapResult
	 *            Specifies whether the result has to be wrapped with a result
	 *            element.
	 * @param accessType
	 *            The {@link EIdAccessType} which indicates the access to a
	 *            special node.
	 */
	private void serializeAT(final File dbFile, final long nodeId,
			final Long revision, final boolean doNodeId,
			final OutputStream output, final boolean wrapResult,
			final EIdAccessType accessType) {
		if (WorkerHelper.checkExistingResource(dbFile)) {
			ISession session = null;
			IDatabase database = null;
			IReadTransaction rtx = null;
			try {
				database = Database.openDatabase(dbFile);
				session = database.getSession();
				if (revision == null) {
					rtx = session.beginReadTransaction();
				} else {
					rtx = session.beginReadTransaction(revision);
				}

				if (rtx.moveTo(nodeId)) {

					switch (accessType) {
					case FIRSTCHILD:
						if (!rtx.moveToFirstChild())
							throw new JaxRxException(404, NOTFOUND);
						break;
					case LASTCHILD:
						if (rtx.moveToFirstChild()) {
							int last = rtx.getNode().getNameKey();
							while (rtx.moveToRightSibling()) {
								last = rtx.getNode().getNameKey();
							}
							rtx.moveTo(last);
						} else {
							throw new JaxRxException(404, NOTFOUND);
						}
						break;
					case RIGHTSIBLING:
						if (!rtx.moveToRightSibling())
							throw new JaxRxException(404, NOTFOUND);
						break;
					case LEFTSIBLING:
						if (!rtx.moveToLeftSibling())
							throw new JaxRxException(404, NOTFOUND);
						break;
					default: // nothing to do;
					}
					if (wrapResult) {
						output.write(BEGINRESULT);
						new XMLSerializer(rtx, output, false, doNodeId).call();

						output.write(ENDRESULT);
					} else {
						new XMLSerializer(rtx, output, false, doNodeId).call();
					}
				} else {
					throw new JaxRxException(404, NOTFOUND);
				}
			} catch (final TreetankException ttExcep) {
				throw new JaxRxException(ttExcep);
			} catch (final IOException ioExcep) {
				throw new JaxRxException(ioExcep);
			} catch (final Exception globExcep) {
				if (globExcep instanceof JaxRxException) { // NOPMD due
					// to
					// different
					// exception
					// types
					throw (JaxRxException) globExcep;
				} else {
					throw new JaxRxException(globExcep);
				}
			} finally {
				try {
					WorkerHelper.closeRTX(rtx, session, database);
				} catch (final TreetankException exce) {
					throw new JaxRxException(exce);
				}
			}

		} else {
			throw new JaxRxException(404, "Resource does not exist");
		}

	}

}