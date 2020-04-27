/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afweb.service.db;

/**
 *
 * @author eddy
 */
public class LockObjectRDB {
    	private String id;
	private String lockname;
	private String type;
	private String lockdatedisplay;
	private String lockdatel;
	private String comment;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
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
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the lockdatedisplay
     */
    public String getLockdatedisplay() {
        return lockdatedisplay;
    }

    /**
     * @param lockdatedisplay the lockdatedisplay to set
     */
    public void setLockdatedisplay(String lockdatedisplay) {
        this.lockdatedisplay = lockdatedisplay;
    }

    /**
     * @return the lockdatel
     */
    public String getLockdatel() {
        return lockdatel;
    }

    /**
     * @param lockdatel the lockdatel to set
     */
    public void setLockdatel(String lockdatel) {
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
