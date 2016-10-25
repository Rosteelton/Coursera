package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    chars(0) = ')'
    chars(1) = '('
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    var i = 0
    var result = 0
    while(i< chars.length) {
      if (chars(i) == '(') result += 1
      if (chars(i) == ')') result -= 1
      if (result < 0) return false
        i += 1
    }
    if (result == 0) true else false
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, acc: Int, index: Int): Int = {
      var i = idx
      var result = 0
      while(i < until) {
        if (chars(i) == '(') result += 1
        if (chars(i) == ')') result -= 1
        if (result < 0) return Int.MaxValue
        i += 1
      }
      result
    }

    def reduce(from: Int, until: Int): Int = {
      if (until - from <= threshold) traverse(from, until, 0, 0)
      else {
        val m = (from + until)/2
        val res = parallel(reduce(from, m), reduce(m, until))
        res._1 + res._2
      }
    }

    reduce(0, chars.length) == 0
  }

}
