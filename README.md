#  Auto configuration for Spring Data Aerospike


## Usage

### Maven configuration

Add the Maven dependency for reactive repository:

```xml
<dependency>
  <groupId>com.aerospike</groupId>
  <artifactId>spring-boot-starter-data-aerospike-reactive</artifactId>  
</dependency>
```

or non-reactive repository:

```xml
<dependency>
  <groupId>com.aerospike</groupId>
  <artifactId>spring-boot-starter-data-aerospike</artifactId>  
</dependency>
```

### Properties

Specify Aerospike host and namespace:

```properties
spring.aerospike.hosts=127.0.0.1:3000
spring.data.aerospike.namespace=COMMON
```

## Example
You can find usage example in *spring-boot-starter-data-aerospike-example* module
