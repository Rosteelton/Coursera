package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = for {
    n <- arbitrary[A]
    h <- frequency((1, empty), (5, genHeap))
  } yield insert(n, h)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("insert and findMin") = forAll { (a: A, b: A) =>
    val heap: H = insert(a, insert(b, empty))
    findMin(heap) == a || findMin(heap) == b
  }

  property("insert and delete") = forAll { (a: A) =>
    deleteMin(insert(a, empty)) == empty
  }

  property("sorted seq") = forAll { (h: H) =>
    def loop(h: H, list: List[A]): Boolean = {
      val min = findMin(h)
      val newHeap = deleteMin(h)
      val newList = min :: list
      if (newList.sortWith((x, y) => x > y) == newList) {
        if (isEmpty(newHeap)) true else
        loop(newHeap, newList)
      } else false
    }
    if (isEmpty(h)) true else loop(h, List.empty[A])
  }

  property("minimum of the melding") = forAll { (h1: H, h2: H) =>
    findMin(meld(h1,h2)) == Math.min(findMin(h1),findMin(h2))
  }

  property("find min") = forAll { (a: A) =>
    findMin(insert(a,empty)) == a
  }

  property("delete min") = forAll { (a: A) =>
    deleteMin(insert(a,empty)) == empty
  }

  property("meld1") = forAll { (h1: H, h2: H, h3: H) =>
    val h12 = meld(h1, h2)
    val h123 = meld(h12, h3)
    findMin(h123) == Math.min(Math.min(findMin(h1), findMin(h2)), findMin(h3))
  }

  property("ordered") = forAll { h: H =>
    @annotation.tailrec
    def loop(acc: List[A], heep: H): List[A] = if (isEmpty(heep)) acc
    else loop(findMin(heep) :: acc, deleteMin(heep))

    val delList: List[Int] = loop(Nil, h)

    @annotation.tailrec
    def loop1(xs1: List[A], xs2: List[A]): Boolean = (xs1, xs2) match {
      case (Nil, Nil) => true
      case (_, Nil) => false
      case (Nil, _) => false
      case (h1 :: t1, h2 :: t2) => (h1 == h2) && loop1(t1, t2)
    }

    loop1(delList, delList.sorted)
  }
}