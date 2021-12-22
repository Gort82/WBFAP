package wrd.ibw.utils;

public class LockEntry {
    private String lockName;
    private int lockLength;
    
    public LockEntry(String pLockName, int pLockLength){
    	this.lockName = pLockName;
    	this.lockLength = pLockLength;
    }
}
