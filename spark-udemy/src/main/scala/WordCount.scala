import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._

/** Count up how many of each word appears in a book as simply as possible. */
object WordCount extends App {
  /** Our main function where the action happens */
  System.setProperty("hadoop.home.dir", "c:\\winutils\\")
  // Set the log level to only print errors
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Create a SparkContext using every core of the local machine
  val sc = new SparkContext("local[*]", "WordCount")

  // Read each line of my book into an RDD
  val input = sc.textFile("c:\\Projects\\udemy-data\\book.txt")

  // Split into words separated by a space character
  val words = input.flatMap(x => x.split(" "))

  // Count up the occurrences of each word
  val wordCounts = words.countByValue()

  // Print the results.
  wordCounts.foreach(println)
}
