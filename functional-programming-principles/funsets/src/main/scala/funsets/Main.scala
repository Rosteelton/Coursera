package funsets

object Main extends App {
  import FunSets._
  //println(contains(singletonSet(1), 6))

  val s1 = union(singletonSet(-2), singletonSet(8))
  val s2 = union(singletonSet(6), singletonSet(4))
  val s3 = union(s1,s2)

      //printSet(s3)
  //println(forall(s3,x => x < 0))

  val new1 = map(s3, x => x*x)

  printSet(new1)

}
