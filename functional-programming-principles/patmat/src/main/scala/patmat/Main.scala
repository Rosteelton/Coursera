package patmat

import patmat.Huffman._

/**
  * Created by ansolovev on 13.07.16.
  */
object Main extends App {

  val list = List('x', 'e', 't', 'x', 'x', 'x', 't')
  val tree = createCodeTree(List('x', 'e', 't', 'x', 'x', 'x', 't'))
  println(tree)


  println(encode(tree)(List('x', 'e', 't', 'x', 'x', 'x', 't')))
  println(decode(tree, List(1, 0, 0, 0, 1, 1, 1, 1, 0, 1)))


  println(Huffman.decodedSecret.mkString(""))


  println(List(0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1) == encode(frenchCode)(List('h', 'u', 'f', 'f', 'm', 'a', 'n', 'e', 's', 't', 'c', 'o', 'o', 'l')))

  println("__________________")

  println(convert(tree))


  println(quickEncode(tree)(list))

}

