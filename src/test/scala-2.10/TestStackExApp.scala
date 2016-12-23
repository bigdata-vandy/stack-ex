import edu.vanderbilt.accre.stackex._
import org.scalatest.WordSpec

/**
  * Created by arnold-jr on 12/21/16.
  */

class TestStackExApp extends WordSpec {

  val line =
    """<row Id="2435" PostTypeId="1" CreationDate="2016-12-06T19:50:13.853"
      |Score="-1" ViewCount="12" Body="&lt;p&gt;If I have a dataset of images,
      |and I extract all cnn feature vectors from them.&#xA;After that I generate
      |the pca model of these features by doing:&lt;/p&gt; &#xA; &#xA; &lt;pre&gt;
      |&lt;code&gt;pca.fit(ALL_features)&#xA; &lt;/code&gt; &lt;/pre&gt; &#xA;
      |&#xA; &lt;p&gt;IF I have a new image and I need to check the similarity
      |between this image and the whole dataset, what I have to do?&lt;/p&gt;
      |&#xA; &#xA; &lt;ol&gt; &#xA; &lt;li&gt;Extract cnn features from this
      |image.&lt;/li&gt; &#xA; &lt;li&gt;How to use the previous pca
      |model?&lt;/li&gt; &#xA; &lt;li&gt;How to check the similarity between
      |the dataset features and the new image features?&lt;/li&gt; &#xA;
      |&lt;/ol&gt; &#xA; &#xA; &lt;p&gt;Is by doing this? or how?&lt;/p&gt;
      |&#xA; &#xA; &lt;pre&gt; &lt;code&gt;self.pca.transform(self.db_feats)&#xA;
      |&lt;/code&gt; &lt;/pre&gt; &#xA;" OwnerUserId="1644"
      |LastActivityDate="2016-12-06T19:50:13.853" Title="PCA pca.fit VS
      |pca.transform" Tags="&lt;machine-learning&gt; &lt;deep-learning&gt;
      |&lt;image-recognition&gt; &lt;conv-neural-network&gt;" AnswerCount="0"
      |CommentCount="0"/>""".stripMargin

  val tagText = """"&lt;machine-learning&gt;&lt;deep-learning&gt;
               |&lt;image-recognition&gt;&lt;conv-neural-network&gt;""""

  "getTags" when {
    "applied to Tag text" should {
      "return a list of tags as String" in {
        assert(getTags(tagText) ==
          List("machine-learning", "deep-learning", "image-recognition",
            "conv-neural-network")
        )
      }
    }
  }

  "getTextFromHtml" when {
    "passed some nested html" should {
      "return the body text in the correct order" in {
        assert(
          getTextFromHtml("<p><em>Emphatic</em><a>excluded </a> parallel</p>") ==
            "Emphatic parallel"
        )
      }
    }
    "passed a valid html snippet" should {
      "return the body text" in {
        assert(getTextFromHtml("<p>some text.</p>") == "some text.")
      }
    }
    "passed some nested html" should {
      "return the body text" in {
        assert(getTextFromHtml("<p><p>some text.</p></p>") == "some text.")
      }
    }
    "passed some heterogeneous nested html" should {
      "return the body text" in {
        assert(getTextFromHtml("<i><p>some text.</p></i>") == "some text.")
      }
    }
    "passed some heterogeneous 3-level-nested html" should {
      "return the body text" in {
        assert(getTextFromHtml("<p><em><i>some text.</i></em></p>") ==
          "some text.")
      }
    }
    "passed some excluded html" should {
      "return the body text" in {
        assert(
          getTextFromHtml("<a>excluded text</a><p>some text.</p>") ==
            "some text."
        )
      }
    }
  }


}
