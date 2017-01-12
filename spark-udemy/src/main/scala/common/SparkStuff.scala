package common

import org.apache.log4j.{Level, Logger}

trait SparkStuff {
  System.setProperty("hadoop.home.dir", "c:\\winutils\\")
  Logger.getLogger("org").setLevel(Level.ERROR)
}
