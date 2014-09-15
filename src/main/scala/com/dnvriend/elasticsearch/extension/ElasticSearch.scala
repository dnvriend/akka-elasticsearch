package com.dnvriend.elasticsearch.extension

import akka.actor.{ExtensionId, ExtensionIdProvider, Extension, ExtendedActorSystem}
import akka.event.Logging
import com.sksamuel.elastic4s.{ElasticDsl, IndexDefinition, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder

import scala.concurrent.Future

object ElasticSearch extends ExtensionId[ElasticSearchImpl] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): ElasticSearchImpl = new ElasticSearchImpl(system)

  override def lookup(): ExtensionId[_ <: Extension] = ElasticSearch

}

trait ElasticSearch extends Extension {

  def createIndex(indexName: String): Future[CreateIndexResponse]

  def deleteIndex(indexName: String): Future[DeleteIndexResponse]

  def doIndex(indexDef: IndexDefinition): Future[IndexResponse]

  def doSearch(searchDef: SearchDefinition): Future[SearchResponse]

}

class ElasticSearchImpl(system: ExtendedActorSystem) extends ElasticSearch {

  private implicit val ec = system.dispatcher

  private val log = Logging(system, this.getClass)

  private val cfg = system.settings.config

  private val settings = ImmutableSettings.settingsBuilder()
    .put("node.data", true)
    .put("node.client", false)
    .put("transport.tcp.port", cfg.getInt("elasticsearch.cluster.port"))
    .put("cluster.name", cfg.getString("elasticsearch.cluster.name"))
    .put("http.port", cfg.getInt("elasticsearch.http.port"))
    .put("http.enabled", cfg.getBoolean("elasticsearch.http.enabled"))
    .put("path.home", cfg.getString("elasticsearch.path.home"))
    .put("es.logger.level", "DEBUG")

  private val node = NodeBuilder.nodeBuilder()
    .clusterName(cfg.getString("elasticsearch.cluster.name"))
    .settings(settings)
    .node()

  val client = ElasticClient.fromNode(node)

  override def doIndex(indexDef: IndexDefinition): Future[IndexResponse] = {
    log.debug("Indexing: {}", indexDef)
    client.execute(indexDef)
  }

  override def doSearch(searchDef: ElasticDsl.SearchDefinition): Future[SearchResponse] = {
    log.debug("Searching: {}", searchDef)
    client.execute(searchDef)
  }

  override def createIndex(indexName: String): Future[CreateIndexResponse] =
    client.execute(create index indexName)

  override def deleteIndex(indexName: String): Future[DeleteIndexResponse] =
    client.execute(delete index indexName)
}


