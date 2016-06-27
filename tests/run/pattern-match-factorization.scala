
object Test {
  def main(args: Array[String]) = {
    def match1(e: Int) = e match {
      case 0 => println("0")
      case 1 => println("1")
      case 2 => println("2")
      case _ => println("3")
    }

    println("match1")
    (0 to 3).foreach(println)

    def match2(e: Int, guard: Boolean = true) = e match {
      case 0 => println("0")
      case 1 => println("1")
      case 2 if guard => println("2")
      case 2 => println("3")
      case 3 => println("4")
      case 4 => println("5")
      case 5 if guard => println("6")
      case _ if guard => println("7")
      case _ => println("8")
    }

    println("match2")
    match2(0)
    match2(1)
    match2(2, true)
    match2(2, false)
    match2(3)
    match2(4)
    match2(5, true)
    match2(5, false)
    match2(6, true)
    match2(6, false)

    def match3(e: Any, guard: Boolean = true) =
      try {
        e match {
          case List(1, 2, 3) if guard => println("0")
          case Some(x) => println("1")
          case List(1, 2, 3) => println("2")
          case List(1, 2) => println("3")
          case 1 => println("4")
          case 2 => println("5")
          case x :: xs if guard => println("6")
          case Nil => println("7")
          case 2 if guard => println("8")
          case _: Int => println("9")
          case 3 => println("10")
          case Some(x) => println("11")
          case None => println("12")
        }
      } catch {
        case e: Throwable => println(e.getMessage)
      }

      println("match3")
      match3(0)
      match3(1)
      match3(2)
      match3(3)
      match3(4)
      match3(List(1, 2, 3))
      match3(List(1, 2, 3), false)
      match3(List(1, 2))
      match3(List(3, 4, 5))
      match3(Nil)
      match3(Some(1))
      match3(Some(1), false)
      match3(Some(1))
      match3("abc")

  }

}
