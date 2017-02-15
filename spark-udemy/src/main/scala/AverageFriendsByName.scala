import common.SparkStuff
import org.apache.spark.SparkContext
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession}


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

  val result = flow.collect().sortBy(_._2).reverse.toSeq

  result.foreach(println)

  //DATASET API

  val sparkConf = SparkSession
    .builder()
    .appName("Friends")
    .master("local[*]")
    .getOrCreate()

  import sparkConf.implicits._

case class Friend(name: String, friendsNumber: Int)
  def getNameAndFriends2: String => Friend = {
    s =>
      val name: String = s.split(",")(1)
      val friends: Int = s.split(",")(3).toInt
      Friend(name, friends)
  }
  val ds: Dataset[Friend] = rdd.map(getNameAndFriends2).toDS()

  import org.apache.spark.sql.functions._
  ds.groupBy("name").avg("friendsNumber").orderBy(desc("avg(friendsNumber)")).show(50)





}
