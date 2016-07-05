def fact(x: Int): BigInt = {

  def byStep(x:Int, acc: BigInt): BigInt = {
    if (x==1) acc else byStep(x-1,acc*x)
  }

  byStep(x,1)
}




fact(30)

