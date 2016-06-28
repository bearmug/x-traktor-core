package org.xtraktor.load

import org.xtraktor.DataPreprocessor

/**
 * Optional database loader implementation. Could be performance-suboptimal
 */
class LoadDataMysql implements LoadData {
    @Override
    void load(DataPreprocessor proc, int precision) {

        // read all users first and put them as stream

        // read point records for each user
        // convert such records to raw data
        // and load them into the system
    }
}
