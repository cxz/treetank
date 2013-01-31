package org.treetank.filelistener.file;

import java.io.File;
import java.nio.file.WatchEvent;
import java.util.Objects;

/**
 * This class is used to safe notifications
 * from the filesystem for later processing via
 * the WorkingQueue.
 * 
 * @author Andreas Rain
 * 
 */
public class FilesystemNotification {

    /** The file that has been changed */
    private final File mFile;

    /** The relative path as a String */
    private final String mRelativePath;

    /** The root path of the listener folder */
    private final String mRootPath;

    /** The event for this notification */
    private final WatchEvent.Kind<?> mEvtType;

    /**
     * Create a FilesystemNotification
     * that holds the File
     * 
     * @param pFile
     * @param pRelativePath
     * @param pRootPath
     */
    public FilesystemNotification(File pFile, String pRelativePath, String pRootPath,
        WatchEvent.Kind<?> pEvtType) {
        super();
        mFile = pFile;
        mRelativePath = pRelativePath;
        mRootPath = pRootPath;
        mEvtType = pEvtType;
    }

    public File getFile() {
        return mFile;
    }

    public String getRelativePath() {
        return mRelativePath;
    }

    public String getRootPath() {
        return mRootPath;
    }

    public WatchEvent.Kind<?> getEvtType() {
        return mEvtType;
    }

    @Override
    public int hashCode(){
        return Objects.hash(mEvtType, mRelativePath, mRootPath, mFile);
    }
    
    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

}