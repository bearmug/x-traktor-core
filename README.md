# x-traktor-core

[![Build Status](https://travis-ci.org/bearmug/x-traktor-core.svg?branch=master)](https://travis-ci.org/bearmug/x-traktor-core) [![codecov](https://codecov.io/gh/bearmug/x-traktor-core/branch/master/graph/badge.svg)](https://codecov.io/gh/bearmug/x-traktor-core) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4d86f8b65caf4cf88a2a84893f534e94)](https://www.codacy.com/app/pavel-fadeev/x-traktor-core?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bearmug/x-traktor-core&amp;utm_campaign=Badge_Grade)


Compact solution to track users routes and find intersections and route 
correlations with other users. Main idea is quite simple and composed of 
two aspects:

1. Any route to be time-boxed. Such as all of it`s points strictly 
aligned in time. For example, route could be converted to points 
sequence, with 10 minutes between each two points. All time-boxed
points assigned with geohash with pre-defined precision. Ergo, full
data set adjusted along time- and geo- grid

2. Correlations and intersection lookups are very straightforward for 
aligned data. Geohash lookup or timestamp lookup takes a little time.

Java streams and closures wildly used across the project. 

## Configuration
Configuration provided at once by means of [LocationConfig](/src/main/groovy/org/xtraktor/location/LocationConfig.groovy)
This configuration noticing required precisions and tolerance levels
around.

## Basic no-extra-coding usage
Just create [CrossTracker](/src/main/groovy/org/xtraktor/CrossTracker.groovy) instance and use it`s [DataPreprocessor](/src/main/java/org/xtraktor/DataPreprocessor.java) 
and [DataMiner](/src/main/java/org/xtraktor/DataMiner.java) interfaces:
```java
 CrossTracker tracker = CrossTracker.create(config, dataStorage)
```

Quite useful and simple case implemented with H2 real data upload and 
import. This scenario could be reviwed under [LoadDataJdbcTest](/src/test-commit/groovy/org/xtraktor/mining/LoadDataJdbcTest.groovy) file:
```groovy
    LoadDataJdbc loader = new LoadDataJdbc(
                connectionString: H2_TEST_CONNECTION_STRING)

    loader.load(CrossTracker.create(config, new SimpleDataStorage()), HASH_PRECISION)
    //OR
    loader.load(CrossTracker.create(config, new RedisDataStorage('localhost', port)), HASH_PRECISION)
    ...
    redisTracker.matchForUser(userId, HASH_PRECISION).each { ... }
```

### Storage modes
In general, underlying storage implements [DataStorage](/src/main/java/org/xtraktor/DataStorage.java) interface and
has to built-in implementations:
 * Java embedded storage, actively using [Guava multimap](https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/Multimap.html) approach. Short
 alt elegant.
```java
  dataStorage = new SimpleDataStorage())
```
 * Storage with plugged [Jedis API](https://github.com/xetorthio/jedis). It is required to connect it to 
 running [Redis](http://redis.io/) instance in order to go ahead. This one providing 
 chance to play around with solution scaling without too much effort.
```java
  dataStorage = new RedisDataStorage('localhost', port))
```
 
## API usage
System is open for extensions over provided interfaces
### Beans storage
Custom persistence could be provided over [DataStorage](/src/main/java/org/xtraktor/DataStorage.java) interface 
implementation to plug it into [CrossTracker](/src/main/groovy/org/xtraktor/CrossTracker.groovy):
```java
    dataStorage = new DataStorage() {
        // custom implememtation
    }
```

### Incoming data pre-processing and mining
[DataPreprocessor](/src/main/java/org/xtraktor/DataPreprocessor.java) and [DataMiner](/src/main/java/org/xtraktor/DataMiner.java) interfaces
could be re-implemented for related functional changes. But since it is
mostly system core, it is recommended to amend existing ones instead.

## Benchmarks
Frankly, most exciting experience across all the project

### JMH microbenchmarks
[JMH](http://openjdk.java.net/projects/code-tools/jmh/) toolkit used to prove concepts and monitor performance for 
segregated classes and components. Please explore gradle 'Jmh' tasks
group for featured cases:
```
gradle tasks
```
These gradle tasks named according to template 'bench<WhichAspectMeasured>'.
For example:
```
gradle benchGroovyJava
```

### Load integrational testing
Here should be short and intuitive demo for system opportunities. 
[Gatling](https://github.com/gatling/gatling) toolkit and relevant Gradle plugin assumed for usage here.
Main KPIs to discover here:
 * Single node saturation metrics
 * Max throughput for single node
 * Ability to stand still against high-load input requests flow

## Integrations
Integrations below are 100% free to use for opensource projects and/or
public github repos.

### Continuous Integration
Basic integration with [Travis CI](https://travis-ci.org/bearmug/x-traktor-core) implemented at master branch
 
### Tests coverage
Code tests coverage continuous measurement implemented with [Codecov](https://codecov.io/gh/bearmug/x-traktor-core)
Implementation uses [JaCoCo](http://www.eclemma.org/jacoco/) output from relevant [Gradle plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)

### Code smells and quality review
Code quality monitored with [Codacy](https://www.codacy.com/app/pavel-fadeev/x-traktor-core/dashboard#)