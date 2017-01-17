import common.SparkStuff
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object Top50SuperHero extends App with SparkStuff {

  val sc = new SparkContext("local[*]", "Superhero")

  def getIdAndName(s: String): Option[(Int, String)] = {
    val splitted = s.split("\"")
    if (splitted.length > 1) {
      Some(splitted(0).trim.toInt, splitted(1))
    } else None
  }

  def getSuperHeroIdAndConnectionsNumber(str: String): (Int, Int) = {
    val splitted = str.split("\\s+")
    (splitted(0).trim.toInt, splitted.length - 1)
  }

  //5983 1165 3836 4361 1282 716 4289 4646 6300 5084 2397 4454 1913 5861 5485
  val relations = sc.textFile("c:\\Projects\\udemy-data\\Marvel-graph.txt")
    .map(getSuperHeroIdAndConnectionsNumber)
    .reduceByKey((x, y) => x + y)
    .map(_.swap)
    .sortByKey(ascending = false) //maxnumber id


  //1 "24-HOUR MAN/EMMANUEL"
  val names = sc.textFile("c:\\Projects\\udemy-data\\Marvel-names.txt")
    .flatMap(getIdAndName)

  val mostPopulars: RDD[(String, Int)] =
    relations.map(some => (names.lookup(some._2).head, some._1))

  //broadCast example
  val storage: Map[String, String] =
    Map("CAPTAIN AMERICA" -> "Yahoo!!! This is the winner")

  val broadсastedStorage: Broadcast[Map[String, String]] =
    sc.broadcast(storage)

  mostPopulars.map {
    case s@(string, count) =>
      if (broadсastedStorage.value.get(string).isDefined)
        (string.concat(" " + broadсastedStorage.value(string)), count)
      else s
  }
    .take(15)
    .foreach(println)
}
