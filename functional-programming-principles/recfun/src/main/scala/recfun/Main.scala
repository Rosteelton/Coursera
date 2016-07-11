package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 15) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
    println()
    println(balance("(())".toList))

  }

  /**
    * Exercise 1
    */
  def pascal(c: Int, r: Int): Int =
    if (c == 0 || c == r || r == 0) 1 else pascal(c - 1, r - 1) + pascal(c, r - 1)


  /**
    * Exercise 2
    */
  def balance(chars: List[Char]): Boolean = {

    def byStep(xs: List[Char], acc: Int): Boolean = {
      xs match {
        case Nil =>
          acc == 0
        case first :: rest =>
          if (acc < 0) false else
          if (first == '(') byStep(rest, acc + 1) else if (first == ')') byStep(rest, acc - 1) else byStep(rest, acc)
      }
    }
    byStep(chars, 0)
  }


  /**
    * Exercise 3
    */

  def countChange(money: Int, coins: List[Int]): Int = {

      if (money == 0) 1 else if (coins.isEmpty || money < 0) 0 else {
        countChange(money - coins.head, coins) + countChange(money, coins.tail)
      }
  }

}




