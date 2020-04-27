package com.vpumlmodel.afweb;

public class ssnslock {

	private int id;
	private String lockname;
	private int type;
	private java.sql.Date lockdatedisplay;
	private long lockdatel;
	private String comment;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the lockname
     */
    public String getLockname() {
        return lockname;
    }

    /**
     * @param lockname the lockname to set
     */
    public void setLockname(String lockname) {
        this.lockname = lockname;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the lockdatedisplay
     */
    public java.sql.Date getLockdatedisplay() {
        return lockdatedisplay;
    }

    /**
     * @param lockdatedisplay the lockdatedisplay to set
     */
    public void setLockdatedisplay(java.sql.Date lockdatedisplay) {
        this.lockdatedisplay = lockdatedisplay;
    }

    /**
     * @return the lockdatel
     */
    public long getLockdatel() {
        return lockdatel;
    }

    /**
     * @param lockdatel the lockdatel to set
     */
    public void setLockdatel(long lockdatel) {
        this.lockdatel = lockdatel;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }


}