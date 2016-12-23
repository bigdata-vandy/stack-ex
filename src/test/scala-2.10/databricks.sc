import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.types.{StringType, StructField, StructType}

val customSchema = StructType(Array(
  StructField("_Id", StringType, nullable = true),
  StructField("_PostTypeId", StringType, nullable = true),
  StructField("_ParentID", StringType, nullable = true),
  StructField("_AcceptedAnswerId", StringType, nullable = true),
  StructField("_CreationDate", StringType, nullable = true),
  StructField("_Score", StringType, nullable = true),
  StructField("_ViewCount", StringType, nullable = true),
  StructField("_Body", StringType, nullable = true),
  StructField("_OwnerUserId", StringType, nullable = true),
  StructField("_LastEditorUserId", StringType, nullable = true),
  StructField("_LastEditorDisplayName", StringType, nullable = true),
  StructField("_LastEditDate", StringType, nullable = true),
  StructField("_LastActivityDate", StringType, nullable = true),
  StructField("_CommunityOwnedDate", StringType, nullable = true),
  StructField("_ClosedDate", StringType, nullable = true),
  StructField("_Title", StringType, nullable = true),
  StructField("_Tags", StringType, nullable = true),
  StructField("_AnswerCount", StringType, nullable = true),
  StructField("_CommentCount", StringType, nullable = true),
  StructField("_FavoriteCount", StringType, nullable = true)
))

val sc = new SparkContext(new SparkConf().setMaster("local(1)").setAppName("foo"))
val sqlContext = new SQLContext(sc)
val df = sqlContext.read
  .format("com.databricks.spark.xml")
  .option("rowTag", "book")
  .load("/Users/joshuaarnold/books.xml")

sc.stop()