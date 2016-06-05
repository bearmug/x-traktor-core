package org.xtraktor.mining;

import org.xtraktor.DataMiner;
import org.xtraktor.HashPoint;
import org.xtraktor.location.LocationConfig;

import java.util.List;

public class SimpleDataMiner implements DataMiner {

    private final LocationConfig config;

    public SimpleDataMiner(LocationConfig config) {
        this.config = config;
    }

    @Override
    public List<HashPoint> matchForPoint(HashPoint input) {
        return null;
    }

    @Override
    public List<HashPoint> matchForRoute(List<HashPoint> input) {
        return null;
    }
}
