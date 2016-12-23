package edu.vanderbilt.accre.stackex

import edu.vanderbilt.accre.xmltools.{loadString, getAttribute}
/**
  * Created by arnold-jr on 12/22/16.
  */
case class Post (id: Int,
                 postTypeId: Int,
                 body: String,
                 score: Int,
                 tags: List[String])

object Post {

  val fieldNames = List("Id", "PostTypeId", "Body", "Score", "Tags")

  def apply(line: String)() = {
    val elem = loadString(line)
    val getAttr = getAttribute(elem)(_)

    new Post(getInt(getAttr("Id")),
      getInt(getAttr("PostTypeId")),
      getTextFromHtml(getAttr("Body")),
      getInt(getAttr("Score")),
      getTags(getAttr("Tags"))
    )
  }

}