import common.SparkStuff
import org.apache.log4j._
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator

import scala.util.{Left, Right}

object GraphRelations extends App with SparkStuff {

  val log = Logger.getLogger("GraphRelations")
  //white gray black
  // BFSData contains an array of hero ID connections, the distance, and color.
  type BFSData = (Vector[Int], Int, Int)

  // A BFSNode has a heroID and the BFSData associated with it.
  type BFSNode = (Int, BFSData)

  //colors
  //black - 3; gray - 2; white - 1

  val sc = new SparkContext("local[*]", "SuperHeroGraph")

  val stopper: LongAccumulator = sc.longAccumulator("stop counter")
  stopper.add(0)

  val rdd: RDD[BFSNode] = sc
    .textFile("c:\\Projects\\udemy-data\\Marvel-graph2.txt")
    .map(toInitialRDD)

  val starHeroId: Int = 1
  val wantToFindId: Int = 6
  val maxNumberDepth = 10

  def toInitialRDD: String => BFSNode = {
    str =>
      val splitted: Array[String] = str.split("\\s+")
      val heroId: Int = splitted(0).toInt
      val connections: Vector[Int] = splitted.tail.toVector.map(_.toInt)
      if (heroId == starHeroId)
        (splitted(0).toInt, (connections, 0, 2))
      else (splitted(0).toInt, (connections, 9999, 1))
  }

  def bfsMap: BFSNode => Vector[BFSNode] = { //work on gray
    node =>
      if (node._2._3 == 2) {
        val changedFirst: BFSNode = (node._1, (Vector.empty[Int], 0, 3))
        val other: Vector[BFSNode] =
          node._2._1.map { heroId =>
            if (heroId == wantToFindId) {
              stopper.add(1)
            }
            (heroId, (Vector.empty[Int], node._2._2 + 1, 2))
          }
        other :+ changedFirst
      } else Vector(node)
  }

  def foldByKeyFunc(a: BFSData, b: BFSData): BFSData = { //preserve darkest color and min distance
    (a._1 ++ b._1, Math.min(a._2, b._2), Math.max(a._3, b._3))
  }

  def findDepthRelation(rdd: RDD[BFSNode], currentDepthNumber: Int): Either[String, Int] = {
    if (currentDepthNumber == 0) Left("The desire connection hasn't been found!")
    else {
      log.info(s"Processing step ${maxNumberDepth - currentDepthNumber + 1}")
      val unfoldGrays = rdd.flatMap(bfsMap)
      log.info(s"Current number of rdd record: ${unfoldGrays.count()}") //important!! only after some action counter will be incremented
      log.info(s"Current rdd:${rdd.collect().toSeq}")
      log.info(s"Currents stopper: ${stopper.value}")
      if (stopper.value > 0) {
        Right(maxNumberDepth - currentDepthNumber + 1)
      } else {
        val newRdd = unfoldGrays.reduceByKey(foldByKeyFunc)
        findDepthRelation(newRdd, currentDepthNumber - 1)
      }
    }
  }

  findDepthRelation(rdd, maxNumberDepth) match {
    case Left(a) => log.info(a)
    case Right(b) => log.info(s"SUCCESS!! FOUND by $b steps")
  }
}
