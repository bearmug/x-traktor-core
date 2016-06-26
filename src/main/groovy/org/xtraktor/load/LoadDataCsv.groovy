package org.xtraktor.load

import groovy.transform.Canonical
import org.xtraktor.DataPreprocessor
import org.xtraktor.RawPoint

import java.nio.file.Paths

@Canonical
class LoadDataCsv implements LoadData {

    public static final String SEPARATOR = ','
    String fileName

    @Override
    void load(DataPreprocessor proc, int precision) {

        Paths.get(fileName).eachLine { line ->

            if (line.trim().empty) {
                return true
            }

            def (lon, lat, time, id) = line.trim().split(SEPARATOR)
            proc.normalize([new RawPoint(
                    longitude: lon as Double,
                    latitude: lat as Double,
                    timestamp: time as Long,
                    userId: id as Long)])
        }
    }
}
