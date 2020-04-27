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
public class CountRowsRDB {
        private String c;

        /**
         * @return the c
         */
        public String getC() {
            return c;
        }

        /**
         * @param c the c to set
         */
        public void setC(String c) {
            this.c = c;
        }    
        public int getCount() {
            int count = Integer.parseInt(this.getC());
            return count;
        }           
}
