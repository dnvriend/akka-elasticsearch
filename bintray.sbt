import bintray.Plugin._

bintray.Plugin.bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("akka", "jdbc", "persistence")

licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("akka", "elasticsearch", "lucene", "cluster", "index", "indexing")
