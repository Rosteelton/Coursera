import common.SparkStuff
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.util.Try

object MovieRecommendations extends App with SparkStuff {

  val sc = new SparkContext("local[*]", "MovieRecommendations")

  val source: RDD[String] = sc.textFile("c:\\Projects\\ml-latest-small\\ratings.csv")

  //userId,movieId,rating,timestamp

  case class FilmData(id: Int, name: String, jenres: Array[String], rating: Double)
  case class FilmInfo(name: String, jenres: Array[String], rating: Double)
  case class Result(score: Double, occurence: Int)

  type userFilmData = (Int, (Int, Double))

  def parse(input: String): Option[userFilmData] = {
    val splitted = input.split(",")
    Try(splitted(0).toInt, (splitted(1).toInt, splitted(2).toDouble)).map(Some(_)).getOrElse(None)
  }

  val ratingsSource: RDD[(Int, (Int, Double))] = source.flatMap(parse)

  def filterOp(row: (Int, (FilmData, FilmData))): Boolean = {
    row match {
      case (user, (film1, film2)) => film1.id < film2.id
    }
  }
    def getFilmPair(tuple: (Int, (FilmData, FilmData))): ((Int, Int), (FilmInfo, FilmInfo)) = {
      tuple match {
        case (user, (film1, film2)) =>
          (
              (film1.id, film2.id),
              (FilmInfo(film1.name, film1.jenres, film1.rating), FilmInfo(film2.name, film2.jenres, film2.rating))
          )
      }
    }

    //score, count
    def calculateSimilarity(tuple: ((Int, Int), Iterable[(FilmInfo, FilmInfo)])):((Int, Int), Result) = {
      tuple match {
        case ((id1, id2), ratings) =>
          val ratingList = ratings.toSeq
          val length = ratingList.length
          val score = ratingList.map(pair => Math.abs(pair._2.rating - pair._1.rating)).sum / length
          ((id1, id2), Result(score, length))
      }
    }

    val desiredFilmId: Int = 33493
    val coOcurenceThr = 40


    def getIdWithName(s: String): Option[(Int, (String, Array[String]))] = {
      val splitted = s.split(",")
      Try(splitted(0).toInt, (splitted(1), splitted(2).split("\\|"))).map(Some(_)).getOrElse(None)
    }

    val namesSource: RDD[(Int, (String, Array[String]))] =
      sc.textFile("c:\\Projects\\ml-latest-small\\movies.csv")
        .flatMap(getIdWithName)
    //movieId,title,genres

    def removeNotSameGenres(tuple: ((Int, Int), (FilmInfo, FilmInfo))) = {
      tuple match {
        case ((id1, id2), (film1, film2)) => film1.jenres.exists(jenre => film2.jenres.contains(jenre))
      }
    }


    val prepare: RDD[(Int, FilmData)] =
      ratingsSource
        .map {
          case (user, (filmId, rating)) =>
            filmId -> (user, rating)
        }
        .join(namesSource)
        .map {
          case (filmId, ((user, rating), (name, jenres))) =>
            user -> FilmData(filmId, name, jenres, rating)
        }

    val recomendationsSorted =
      prepare.join(prepare)
        .filter(filterOp)
        .map(getFilmPair)
        .filter(removeNotSameGenres)
        .groupByKey()
        .map(calculateSimilarity)
        .cache()
        .collect {
          case ((film1, film2), result) if film1 == desiredFilmId && result.occurence > coOcurenceThr =>
            film2 -> result.score
          case ((film1, film2), result) if film2== desiredFilmId && result.occurence > coOcurenceThr =>
            film1 -> result.score
        }.map {
        case (film, score) => score -> film
      }
        .sortByKey()
        .take(10)
        .toList


    val desiredFilm: (String, Array[String]) = namesSource.lookup(desiredFilmId).headOption.getOrElse("Name Not Found", Array("Name Not Found"))
    println(s"Recommendations for film ${desiredFilm._1} (jenre: ${desiredFilm._2.mkString(", ")}) are:")


    recomendationsSorted.map {
      case (score, filmId) =>
        val film = namesSource.lookup(filmId).head
        s"score: $score, film: ${film._1}, jenres: ${film._2.mkString(", ")}"
    }
      .foreach(println)
  }