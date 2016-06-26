package org.xtraktor.load

import groovy.transform.Canonical
import org.xtraktor.DataPreprocessor
import org.xtraktor.HashPoint

import java.nio.file.Paths
import java.util.stream.Stream

@Canonical
class LoadDataCsv implements LoadData {

    public static final String SEPARATOR = ','
    private final String fileName

    @Override
    void load(DataPreprocessor proc, int precision) {
        Paths.get(fileName).eachLine { line ->
            def (hash, lon, lat, time, it) = line.trim().split(SEPARATOR)
            new HashPoint(geoHashFull: hash, longitude: lon, latitude: lat, timestamp: time, userId: id)
        }
    }
}
