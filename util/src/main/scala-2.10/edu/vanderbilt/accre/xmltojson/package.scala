package edu.vanderbilt.accre

/**
  * Created by arnold-jr on 12/20/16.
  */
package object xmltojson {

  type AttributeMapper = Map[String, String => Any]

  trait AttributeCollection[T] {
    def getAttributeMapper(t: T): AttributeMapper
  }

  object AttributeCollection {

    implicit object AttributeList extends AttributeCollection[List[String]] {
      def getAttributeMapper(attrList: List[String]) = {
        val ident: String => String = (s: String) => s
        val empty: AttributeMapper = Map.empty
        attrList.foldLeft(empty) {
          (m, v) => m + (v -> ident)
        }
      }
    }
    implicit object AttributeMap
      extends AttributeCollection[AttributeMapper] {
      def getAttributeMapper(attrMapper: AttributeMapper) = attrMapper
    }
  }
}
