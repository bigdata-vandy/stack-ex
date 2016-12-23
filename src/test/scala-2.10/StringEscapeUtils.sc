import org.apache.commons.lang.StringEscapeUtils.{escapeHtml, unescapeHtml}

escapeHtml("<neural-networks><definitions>")

escapeHtml("&lt;neural&gt;")

val text = "&lt;neural-networks&gt;&lt;definitions&gt;"

val e = escapeHtml(text)
val u = unescapeHtml(text)

val tagPattern = "(?<=&lt;)\\S+?(?=&gt;)".r
(tagPattern findAllIn text) toList

(tagPattern findAllIn e) toList

(tagPattern findAllIn u) toList

