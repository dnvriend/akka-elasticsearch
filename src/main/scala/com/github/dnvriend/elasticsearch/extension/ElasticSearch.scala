/*
 * Copyright 2015 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.elasticsearch.extension

import java.net.URL

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import akka.event.{ LoggingAdapter, Logging }
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ ElasticClient, IndexDefinition, SearchDefinition }
import com.typesafe.config.Config
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder

import scala.concurrent.{ ExecutionContext, Future }

object ElasticSearch extends ExtensionId[ElasticSearchImpl] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): ElasticSearchImpl = new ElasticSearchImpl(system)

  override def lookup(): ExtensionId[_ <: Extension] = ElasticSearch

}

trait ElasticSearch extends Extension {

  def createIndex(indexName: String): Future[CreateIndexResponse]

  def deleteIndex(indexName: String): Future[DeleteIndexResponse]

  def doIndex(indexDef: IndexDefinition): Future[IndexResponse]

  def doSearch(searchDef: SearchDefinition): Future[SearchResponse]

  def client: ElasticClient
}

class ElasticSearchImpl(system: ExtendedActorSystem) extends ElasticSearch {

  private implicit val ec: ExecutionContext = system.dispatcher

  private val log: LoggingAdapter = Logging(system, this.getClass)

  private val cfg: Config = system.settings.config.getConfig("elasticsearch")

  /**
   * Load settings from the classpath
   * @param path
   * @return
   */
  def loadFromClasspath(path: String) =
    ImmutableSettings
      .settingsBuilder()
      .loadFromClasspath(path)

  /**
   * Loads settings from an URL
   * @param url
   * @return
   */
  def loadFromUrl(url: String) =
    ImmutableSettings
      .settingsBuilder()
      .loadFromUrl(new URL(url))

  object SettingsRegex {
    val classpath = """classpath://(.*)""".r
    val url = """(file://|http://).*""".r
  }

  private def settings: ImmutableSettings.Builder = {
    val elasticSearchYamlPath = cfg.getString("settings")
    elasticSearchYamlPath match {
      case SettingsRegex.classpath(path) ⇒ loadFromClasspath(path)
      case SettingsRegex.url(_*)         ⇒ loadFromUrl(elasticSearchYamlPath)
    }
  }

  private val node = NodeBuilder.nodeBuilder()
    .settings(settings)
    .node()

  val client = ElasticClient.fromNode(node)

  override def doIndex(indexDef: IndexDefinition): Future[IndexResponse] =
    client.execute(indexDef)

  override def doSearch(searchDef: SearchDefinition): Future[SearchResponse] =
    client.execute(searchDef)

  override def createIndex(indexName: String): Future[CreateIndexResponse] =
    client.execute(create index indexName)

  override def deleteIndex(indexName: String): Future[DeleteIndexResponse] =
    client.execute(delete index indexName)
}
