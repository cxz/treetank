package org.treetank.encryption;

import java.util.LinkedList;
import java.util.List;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * This class represents the key selector model holding all data
 * for a node of the right tree consisting of group and user nodes.
 * 
 * @author Patrick Lang, University of Konstanz
 */
@Entity
public class KeySelector {

    /**
     * Selector key and primary key of database.
     */
    @PrimaryKey
    private long mSelectorKey;

    /**
     * Name of the node (group or user name).
     */
    private String mName;

    /**
     * List of parent nodes.
     */
    private LinkedList<Long> mParents;

    /**
     * List of child nodes.
     */
    private LinkedList<Long> mChilds;

    /**
     * Current revision of node.
     */
    private int mRevision;

    /**
     * Current version of node.
     */
    private int mVersion;

    private EntityType mType;

    /**
     * Secret key using for data en-/decryption.
     */
    private byte[] mSecretKey;

    /**
     * Standard constructor.
     */
    public KeySelector() {
        super();
    }

    /**
     * Constructor for building an new key selector instance.
     * 
     * @param paramName
     *            node name.
     */
    public KeySelector(final String paramName, final LinkedList<Long> paramPar,
        final LinkedList<Long> paramChild, final int paramRev,
        final int paramVer, final EntityType paramType) {
        this.mSelectorKey = PrimaryKeyGenerator.getInstance().newSelectorKey();
        this.mName = paramName;
        this.mParents = paramPar;
        this.mChilds = paramChild;
        this.mRevision = paramRev;
        this.mVersion = paramVer;
        this.mType = paramType;
        this.mSecretKey = new NodeEncryption().generateSecretKey();
    }

    /**
     * Returns selector id.
     * 
     * @return
     *         selector id.
     */
    public final long getPrimaryKey() {
        return mSelectorKey;
    }

    /**
     * Returns node name.
     * 
     * @return
     *         node name.
     */
    public final String getName() {
        return mName;
    }

    /**
     * Returns a list of parent nodes for node.
     * 
     * @return
     *         set of parent nodes.
     */
    public final LinkedList<Long> getParents() {
        return mParents;
    }

    /**
     * Returns a list of child nodes for node.
     * 
     * @return
     *         set of child nodes.
     */
    public final LinkedList<Long> getChilds() {
        return mChilds;
    }

    /**
     * Adds a new parent node to the list.
     * 
     * @param paramParent
     *            parent to add to list.
     */
    public final void addParent(final long paramParent) {
        mParents.add(paramParent);
    }

    /**
     * Adds a new child node to the list.
     * 
     * @param paramParent
     *            parent to add to list.
     */
    public final void addChild(final long paramChild) {
        mChilds.add(paramChild);
    }

    /**
     * Removes a parent node from list.
     * 
     * @param paramParent
     *            parent node to remove.
     */
    public void removeParent(final long paramParent) {
        mParents.remove(paramParent);
    }

    /**
     * Removes a child node from list.
     * 
     * @param paramChild
     *            child node to remove.
     */
    public void removeChild(final long paramChild) {
        mChilds.remove(paramChild);
    }

    /**
     * Returns current revision of node.
     * 
     * @return
     *         node's revision.
     */
    public final int getRevision() {
        return mRevision;
    }

    /**
     * Increases node revision by 1.
     */
    public final void increaseRevision() {
        this.mRevision += 1;
    }

    /**
     * Returns current version of node.
     * 
     * @return
     *         node's version.
     */
    public final int getVersion() {
        return mVersion;
    }

    /**
     * Increases node version by 1.
     */
    public final void increaseVersion() {
        this.mVersion += 1;
    }

    /**
     * Returns type of entity.
     */
    public EntityType getType() {
        return mType;
    }

    /**
     * Returns secret key.
     * 
     * @return
     *         secret key.
     */
    public final byte[] getSecretKey() {
        return mSecretKey;
    }
}
