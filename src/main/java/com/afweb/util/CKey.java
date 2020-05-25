package com.afweb.util;

public class CKey {

    //local pc
    public static String FileLocalPathTemp = "C:\\TEMP\\";

    public static final boolean LocalPCflag = true; // true;
//sh-4.2$ env | grep MYSQL
//MYSQL_PREFIX=/opt/rh/rh-mysql57/root/usr
//MYSQL_VERSION=5.7
//MYSQL_DATABASE=sampledb
//MYSQL_PASSWORD=admin
//MYSQL_PORT_3306_TCP_PORT=3306
//MYSQL_PORT_3306_TCP=tcp://100.65.177.29:3306
//MYSQL_SERVICE_PORT_MYSQL=3306
//MYSQL_PORT_3306_TCP_PROTO=tcp
//MYSQL_PORT_3306_TCP_ADDR=100.65.177.29
//MYSQL_SERVICE_PORT=3306
//MYSQL_USER=sa
//MYSQL_ROOT_PASSWORD=admin
//MYSQL_PORT=tcp://100.65.177.29:3306
//MYSQL_SERVICE_HOST=100.65.177.29
//sh-4.2$    
    public static final String MYSQL_SERVICE_HOST = "100.65.177.29";
    //////////////////////
    // remember to update the application properties      
    public static final int LOCAL_MYSQL = 4; //jdbc:mysql://localhost:3306/db_sample       
    public static final int REMOTE_MYSQL = 2; // https://eddyko.000webhostapp.com/webgetreq.php php mysql
    public static final int MYSQL = 0;   //direct mysql   

    public static final int POSTGRESQLDB = 1; //using LOCAL_MYSQL require this set
    public static final int MYSQLDB = 0;
    public static int DB = MYSQLDB;

    public static final int SQL_DATABASE = MYSQL;
    public static final boolean SQL_RemoveServerDB = false; // need true and REMOTE_MYSQL using remote server not PHP 
    //
    //////////////////////
    //
    //always false
    public static boolean PROXY = false; //always false; 
    public static String PROXYURL_TMP = "webproxystatic-on.tslabc.tabceluabcs.com";
    //always false 

    public static String URL_PRODUCT_TMP = "https://sabcoa-mp-rmsabck-prabc.tsabcl.teabclus.com";

    public static boolean NN_DEBUG = false; //false; //true; 
    public static boolean UI_ONLY = false;
    public static boolean DEVOP = false;

//    //
//    //
    public static String WEBPOST_OP_PHP = "/health.php";
    public static String URL_PATH_OP_DB_PHP1_TMP = "http://devphp-project001.paas-app-east-np.tabcsl.telabcus.com";
    public static final String URL_PATH_OP_TMP = "http://devssns-project001.paas-app-east-np.tabcsl.telabcus.com";
//    public static String URL_PATH_OP_DB_PHP1_TMP = "http://devphp-web007.apps.us-east-1.starter.openshift-online.com";      
//    public static final String URL_PATH_OP_TMP = "http://iisweb-web007.apps.us-east-1.starter.openshift-online.com";

//
    public static String dataSourceURL = "";
    public static final String ADMIN_USERNAME = "Admin1";
////////////////////////////  
////////////////////////////    
    public static final String COMMA = ",";
    public static final String MSG_DELIMITER = "~";
    public static final String QUOTE = "'";
    public static final String DASH = "-";
    public static final String DB_DELIMITER = "`";

    public CKey() {

    }

}
