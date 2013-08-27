package org.treetank.jscsi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This file storage is used for faster access
 * within the treetank iscsi module.
 * 
 * @author Andreas Rain
 * 
 */
public class FileStorage {

    /** The base directory for the storage. */
    private final String mBaseDir;

    /** Filesize for each file (can be used to reflect nodes) */
    private final int mFileSize;

    /** File cache */
    private Cache<Integer, byte[]> mCache;

    /** How many file contents have to be cached. */
    private final int CACHE_SIZE = 256;

    /**
     * @param pBaseDir
     *            - The root directory for the filestorage (will be created if not exists)
     * @param pFileSize
     *            - The size of each file
     * @throws IOException
     */
    public FileStorage(String pBaseDir, int pFileSize) throws IOException {
        super();
        mBaseDir = pBaseDir;
        mFileSize = pFileSize;

        File file = new File(pBaseDir);
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            throw new IOException("The parameter pBaseDir does not seem to be a directory.");
        }

        mCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build();
    }

    /**
     * Reading bytes from file
     * @param bytes
     * @param storageIndex
     * @throws IOException
     */
    public void read(byte[] bytes, long storageIndex) throws IOException {

        long filePos = storageIndex / mFileSize;
        int storageOffset = (int)(storageIndex % mFileSize);
        byte[] cachedBytes = mCache.getIfPresent(filePos);

        File fileAtPos = new File(mBaseDir+File.separator+filePos);
        if(!fileAtPos.exists()){
            fileAtPos.createNewFile();
            cachedBytes = new byte[mFileSize];
            Files.write(fileAtPos.toPath(), cachedBytes);
        }
        else if (cachedBytes == null) {
            cachedBytes = Files.readAllBytes(fileAtPos.toPath());
            mCache.put((int)filePos, cachedBytes);
        }

        if ((storageOffset + bytes.length) > mFileSize) {
            System.arraycopy(cachedBytes, storageOffset, bytes, 0, mFileSize - storageOffset);
            byte[] nextStep = new byte[bytes.length - (mFileSize - storageOffset)];
            read(nextStep, storageIndex + (mFileSize - storageOffset));
            System.arraycopy(nextStep, 0, bytes, mFileSize - storageOffset, nextStep.length);
        } else {
            System.arraycopy(cachedBytes, storageOffset, bytes, 0, bytes.length);
        }

    }
    
    /**
     * Writing into a file.
     * @param bytes
     * @param storageIndex
     * @throws IOException
     */
    public void write(byte[] bytes, long storageIndex) throws IOException {

        long filePos = storageIndex / mFileSize;
        boolean cached = true;
        int storageOffset = (int)(storageIndex % mFileSize);
        byte[] cachedBytes = mCache.getIfPresent(filePos);

        File fileAtPos = new File(mBaseDir+File.separator+filePos);
        if(!fileAtPos.exists()){
            fileAtPos.createNewFile();
            cachedBytes = new byte[mFileSize];
        }
        else if (cachedBytes == null) {
            cachedBytes = Files.readAllBytes(fileAtPos.toPath());
            cached = false;
        }

        if ((storageOffset + bytes.length) > mFileSize) {
            System.arraycopy(bytes, 0, cachedBytes, storageOffset, mFileSize - storageOffset);
            
            Files.write(new File(mBaseDir+File.separator+filePos).toPath(), cachedBytes);
            
            byte[] nextStep = new byte[bytes.length - (mFileSize - storageOffset)];
            System.arraycopy(bytes, (mFileSize - storageOffset), nextStep, 0, bytes.length
                - (mFileSize - storageOffset));
            write(nextStep, storageIndex + (mFileSize - storageOffset));
        } else {
            System.arraycopy(bytes, 0, cachedBytes, storageOffset, bytes.length);
            Files.write(fileAtPos.toPath(), cachedBytes);
        }
        
        if(!cached){
            mCache.put((int)filePos, cachedBytes);
        }
        
    }

}
