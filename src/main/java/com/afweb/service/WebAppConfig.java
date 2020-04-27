package com.afweb.service;

import com.afweb.util.CKey;
import com.afweb.util.getEnv;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class WebAppConfig {

    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
         if (CKey.SQL_DATABASE == CKey.MYSQL) {
          
//eddy corp            
//sh-4.2$ env | grep MYSQL
//MYSQL_PREFIX=/opt/rh/rh-mysql57/root/usr
//MYSQL_VERSION=5.7
//MYSQL_DATABASE=sampledb
//MYSQL_PASSWORD=admin
//MYSQL_PORT_3306_TCP_PORT=3306
//MYSQL_PORT_3306_TCP=tcp://100.65.146.239:3306
//MYSQL_SERVICE_PORT_MYSQL=3306
//MYSQL_PORT_3306_TCP_PROTO=tcp
//MYSQL_PORT_3306_TCP_ADDR=100.65.146.239
//MYSQL_SERVICE_PORT=3306
//MYSQL_USER=sa
//MYSQL_ROOT_PASSWORD=admin
//MYSQL_PORT=tcp://100.65.146.239:3306
//MYSQL_SERVICE_HOST=100.65.146.239
//sh-4.2$


            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            String dsUrl = "jdbc:mysql://100.65.146.239:3306/sampledb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
            dataSource.setUrl(dsUrl);
            dataSource.setUsername("sa");
            dataSource.setPassword("admin");

            if (ServiceAFweb.URL_LOCALDB.length() == 0) {
                ServiceAFweb.URL_LOCALDB = dataSource.getUrl();
            } else {
                 dataSource.setUrl(ServiceAFweb.URL_LOCALDB);
            }
        }

        CKey.dataSourceURL = dataSource.getUrl();
        return dataSource;
    }
}
