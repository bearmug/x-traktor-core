package org.xtraktor.load

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.xtraktor.DataPreprocessor
import org.xtraktor.RawPoint

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement

/**
 * Optional database loader implementation. Could be performance-suboptimal
 */
@Slf4j
@Canonical
class LoadDataJdbc implements LoadData {

    private String connection;
    private String username;
    private String pw;

    @Override
    void load(DataPreprocessor proc, int precision) {

        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://$connection?" +
                        "useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&" +
                        "useLegacyDatetimeCode=false&" +
                        "serverTimezone=UTC&" +
                        "user=$username&password=$pw")
        Statement stmt = connection.createStatement()

        // read all users first and put them as stream
        stmt.executeQuery 'select distinct(user_id) from gps_tracks'

        while (stmt.resultSet.next()) {
            long userId = stmt.resultSet.getLong 'user_id'
            Statement userStmt = connection.createStatement()

            log.trace "Loading raw entries for userId: $userId"
            userStmt.executeQuery(
                    "select latitude, longtitude, time from gps_tracks " +
                            "where " +
                            "user_id = $userId " +
                            "and time != 0 " +
                            "order by time")

            List<RawPoint> res = []
            while (userStmt.resultSet.next()) {
                double latitude = userStmt.resultSet.getDouble 'latitude'
                double longitude = userStmt.resultSet.getDouble 'longtitude'
                long time = userStmt.resultSet.getLong 'time'

                res.add(new RawPoint(
                        longitude: longitude,
                        latitude: latitude,
                        timestamp: time,
                        userId: userId
                ))
            }
            log.debug "Loaded raw entries for userId: $userId, total entries number: $res.size"
            proc.normalize res
        }
    }
}
