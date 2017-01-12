import common.SparkStuff
import org.apache.spark.SparkContext

object AverageFriendsByName extends App with SparkStuff {

  val sc = new SparkContext("local[*]", "Friends")

  val rdd = sc.textFile("c:\\Projects\\udemy-data\\fakefriends.csv")

  def getNameAndFriends: String => (String, Int) = {
    s =>
      val name: String = s.split(",")(1)
      val friends: Int = s.split(",")(3).toInt
      (name, friends)
  }

  val flow =
    rdd.map(getNameAndFriends)
      .mapValues(int => (int, 1))
      .reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
      .mapValues(pair => pair._1 / pair._2)

  val result = flow.collect().sortBy(_._1).toSeq

  result.foreach(println)
}
