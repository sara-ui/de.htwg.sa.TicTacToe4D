
object ioWorksheet {

  val value = "X"
  def toXml: Unit = {
    <cell value={value}></cell>
  }

  print(toXml)

}