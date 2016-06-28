# x-traktor-core
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
Just create [CrossTracker]() instance and use it`s [DataPreprocessor]() 
and [DataMiner]() interfaces:
```java
 CrossTracker tracker = new CrossTracker()
```

### Storage modes
In general, underlying storage implements [DataStorage]() interface and
has to built-in implementations:
 * Java embedded storage, actively using [Guava multiset]() approach. Short
 alt elegant.
 * Storage with plugged [Jedis API]. It is required to connect it to 
 running [Redis]() instance in order to go ahead. This one providing 
 chance to play around with solution scaling without too much effort.
 
## API usage
### Beans storage
### Incoming data pre-processing
### Mining and lookup
## Benchmarks
I do love benchmarks generation. And benchmark outputs is the most 
exciting part for this kind of activity.

### JMH microbenchmarks
[JMH]() toolkit used to prove concepts and monitor performance for 
segregated classes and components. Please explore gradle 'Jmh' tasks
group for featured cases:
```
gradle tasks
```
These gradle tasks named according to template 'bench<WhichAspectMeasured>'.
For example:
'''
gradle benchGroovyJava
'''

### Load integrational testing
Here should be short and intuitive demo for system opportunities. 
[Gatling]() toolkit and relevant Gradle plugin assumed for usage here.
Main KPIs to discover here:
 * Single node saturation metrics
 * Max throughput for single node
 * Ability to stand still against high-load input requests flow