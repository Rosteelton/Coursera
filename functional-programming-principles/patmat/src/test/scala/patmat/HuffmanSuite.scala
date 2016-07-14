package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {

  trait TestTrees {
    val t1 = Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5)
    val t2 = Fork(Fork(Leaf('a', 2), Leaf('b', 3), List('a', 'b'), 5), Leaf('d', 4), List('a', 'b', 'd'), 9)
  }


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a', 'b', 'd'))
    }
  }

  test("times ") {
    new TestTrees {
      assert(times(List('d', 'a', 'b', 'c', 'a', 'b', 'b')) === List(('d', 1), ('a', 2), ('b', 3), ('c', 1)))
    }
  }

  test("makeOrderedLeafList") {

    assert(makeOrderedLeafList(List(('d', 1), ('a', 2), ('b', 3), ('c', 1))) === List(Leaf('d', 1), Leaf('c', 1), Leaf('a', 2), Leaf('b', 3)))
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }


  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 3)))
  }


  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4)))
  }

test("createCodeTree"){

  assert(createCodeTree(List('e', 't', 't', 'x', 'x', 'x', 'x')) === Fork(Fork(Leaf('e', 1), Leaf('t', 2), List('e', 't'), 3), Leaf('x', 4),List('e','t','x'),7))
}


  test("decode and encode a very short text should be identity") {
    new TestTrees {
      assert(decode(t1, encode(t1)("ab".toList)) === "ab".toList)
    }
  }

  test("more complicated test (quickEncode)") {
    new TestTrees {
      val txt = "Attention: Once you have submitted your solution, you should see your grade and a feedback about your code on the Coursera website within 10 minutes. If you want to improve your grade, just submit an improved solution."
      val chars = string2Chars(txt)
      val tree = createCodeTree(chars)
      private val enc = quickEncode(tree)(chars)
      private val dec = decode(tree, enc)
      assert(dec === chars)
    }
  }

}
