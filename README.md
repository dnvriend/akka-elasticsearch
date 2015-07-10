# akka-elasticsearch
An ElasticSearch extension for Akka. The extension registers a single instance of the elasticsearch client as node,
that can be configured by an `application.conf` that points to a `elasticsearch.yml` file. Using docker containers
the `elasticsearch.yml` can be loaded from the file system, url or classpath. The extension makes use of the work
done by [Stephen Samuel](https://github.com/sksamuel), many thanks to him and the committers of 
[elastic4s - Elasticsearch Scala Client](https://github.com/sksamuel/elastic4s), thanks thanks thanks!!

[![Build Status](https://travis-ci.org/dnvriend/akka-elasticsearch.svg?branch=master)](https://travis-ci.org/dnvriend/akka-elasticsearch)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Dependency
To include the akka-elasticsearch extension to your project, add the following lines to your build.sbt:
 
 ```bash
resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

libraryDependencies += "com.github.dnvriend" %% "akka-elasticsearch" % "1.0.4"
```

# What's new?

### v1.0.5 (2015-07-10)
  - akka v2.3.11 -> v2.3.12
  - elastic4s v1.6.0 -> v1.6.4

### v1.0.4 (2015-07-06)
  - Took the project out of the mothballs, using it in production,
  - Loading config from classpath, directory or url,
  - elastic4s v1.3.2 -> v1.6.0                                   

# Usage
Launch the extension:

```scala
import com.github.dnvriend.elasticsearch.extension.ElasticSearch
import com.sksamuel.elastic4s.ElasticClient

val system = ActorSystem()
val elasticSearch = ElasticSearch(system)
val client: ElasticClient = elasticSearch.client
```
You can also register the extension in application.conf:

```bash
akka {
    extensions = ["com.github.dnvriend.elasticsearch.extension.ElasticSearch"]
}
```

# Configure the elasticsearch node
The extension can be configured by means of `application.conf`. You have to configure the path to the 
`elasticsearch.yml` file, that can be on an URL (like etc.d), the filesystem (docker container) or classpath.

```bash
elasticsearch {
  settings = "classpath://elasticsearch.yml"
}
```

# Execute a query
To execute a query:

```scala
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder

val mySearchDef = search in "orders" -> "order" sort { by field "created" order SortOrder.DESC} limit 100

val response: Future[SearchResponse] = elasticSearch.doSearch(mySearchDef)
```

# To index
To index:

```scala
import com.sksamuel.elastic4s.source.DocumentMap

case class Order(orderId: String, created: String) extends DocumentMap {
  def map = Map("orderId" -> orderId, "created" -> created)
}

val myIndexDefinition = index into "orders" -> "order" doc Order(UUID.randomUUID.toString, "2014-01-01")

val response: Future[IndexResponse] = elasticSearch.doIndex(myIndexDefinition)
```
