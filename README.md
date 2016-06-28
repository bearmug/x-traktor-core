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
Configuration provided at once by means of [LocationConfig](bearmug#/x-traktor-core/blob/master/src/main/groovy/org/xtraktor/location/LocationConfig.groovy)
## Basic no-extra-coding usage
### Storage modes
## API usage
### Beans storage
### Incoming data preprocessing
### Mining and lookup
## Benchmarks
### JMH microbenchmarks
### Load integrational testing
