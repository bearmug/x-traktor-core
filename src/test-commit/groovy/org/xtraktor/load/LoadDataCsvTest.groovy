package org.xtraktor.load

import org.xtraktor.DataPreprocessor
import spock.lang.Specification
import spock.lang.Unroll

class LoadDataCsvTest extends Specification {

    private static final String RES_LOCATION = 'src/test-commit/resources/csv/'

    @Unroll
    def "correct file #file parsed"() {
        given:
        LoadData loader = new LoadDataCsv(fileName: RES_LOCATION + file)
        DataPreprocessor proc = Mock(DataPreprocessor)

        when:
        loader.load(proc, precision)

        then:
        2 * proc.normalize(_)

        where:
        file          | precision
        'valid-1.csv' | 6
        'valid-2.csv' | 6
    }

    def "file with invalid/un-parseable content not parsed"() {
        given:
        LoadData loader = new LoadDataCsv(fileName: RES_LOCATION + file)
        DataPreprocessor proc = Mock(DataPreprocessor)

        when:
        loader.load(proc, precision)

        then:
        thrown NumberFormatException

        where:
        file            | precision
        'invalid-1.csv' | 6
        'invalid-2.csv' | 6
    }

    def "exception interrupts input processing"() {
        given:
        LoadData loader = new LoadDataCsv(fileName: RES_LOCATION + file)
        DataPreprocessor proc = Mock(DataPreprocessor)

        when:
        loader.load(proc, precision)

        then:
        1 * proc.normalize(_)
        thrown NumberFormatException

        where:
        file            | precision
        'invalid-3.csv' | 6
    }
}
