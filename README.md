# akka-elasticsearch
The ElasticSearch extension for Akka

# Usage
Launch the extension:

    val system = ActorSystem("MySystem")
    val elasticSearch = ElasticSearch(system)


# Execute a query
To execute a query:

    import com.sksamuel.elastic4s.ElasticDsl._
    import org.elasticsearch.search.sort.SortOrder
    
    val mySearchDef = search in "orders" -> "order" sort { by field "created" order SortOrder.DESC} limit 100
    
    val response: Future[SearchResponse] = elasticSearch.doSearch(mySearchDef)


# To index
To index:

    import com.sksamuel.elastic4s.source.DocumentMap
    
    case class Order(orderId: String, created: String) extends DocumentMap {
      def map = Map("orderId" -> orderId, "created" -> created)
    }
    
    val myIndexDefinition = index into "orders" -> "order" doc Order(UUID.randomUUID.toString, "2014-01-01")
    
    val response: Future[IndexResponse] = elasticSearch.doIndex(myIndexDefinition)
