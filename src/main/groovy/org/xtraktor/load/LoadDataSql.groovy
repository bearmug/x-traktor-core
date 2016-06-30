package org.xtraktor.load

import groovy.transform.Canonical
import org.xtraktor.DataPreprocessor

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

/**
 * Optional database loader implementation. Could be performance-suboptimal
 */
@Canonical
class LoadDataSql implements LoadData {

    private String connString;
    private String username;
    private String pw;

    @Override
    void load(DataPreprocessor proc, int precision) {


        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://${connString}?" +
                        "useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&" +
                        "serverTimezone=UTC&" +
                        "user=$username&password=$pw")
        Statement stmt = connection.createStatement()

        // read all users first and put them as stream
        stmt.executeQuery('select distinct(user_id) from gps_tracks')

        long counter = 0

        while (stmt.resultSet.next()) {
            long userId = stmt.resultSet.getLong('user_id')
            Statement userStmt = connection.createStatement()
            userStmt.executeQuery(
                    "select latitude, longtitude, time from gps_tracks " +
                            "where " +
                            "user_id = ${userId} " +
                            "and time != 0 " +
                            "order by time")

            while (userStmt.resultSet.next()) {
                double latitude = userStmt.resultSet.getDouble('latitude')
                double longitude = userStmt.resultSet.getDouble('longtitude')
                long time = userStmt.resultSet.getLong('time')

                counter++
            }
        }

        println "Total: $counter"
    }
}
