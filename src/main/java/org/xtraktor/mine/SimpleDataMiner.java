package org.xtraktor.mine;

import org.xtraktor.DataMiner;
import org.xtraktor.ShortPoint;
import org.xtraktor.location.LocationConfig;

import java.util.List;

public class SimpleDataMiner implements DataMiner {

    private final LocationConfig config;

    public SimpleDataMiner(LocationConfig config) {
        this.config = config;
    }

    @Override
    public List<ShortPoint> matchForPoint(ShortPoint input) {
        return null;
    }

    @Override
    public List<ShortPoint> matchForRoute(List<ShortPoint> input) {
        return null;
    }
}
