package eu.bde.template.maven

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row}


object WordCount {

  /** Usage: WordCount [file] */
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: dataset <file>")
      System.exit(1)
    }
    println("Index dataset extension job here.")

    val spark = SparkSession
      .builder
      .appName("IndexExtensionJob")
      .config("spark.executor.memory", "4g")
      .getOrCreate()

    import spark.implicits._

    /*
    val file = spark.read.parquet(args(0))
    val result = file.withColumn("index",monotonicallyIncreasingId())
    result.write.parquet("/flights-indexed.parquet")
    */

    val file = spark.read.parquet(args(0))
    println("addColumnIndex here")
    // Add index now...
    val df1WithIndex = addColumnIndex(file, spark).withColumn("monotonically_increasing_id", monotonically_increasing_id)
    //df1WithIndex.show(false)
    df1WithIndex.write.parquet("/flights-indexed.parquet")
    println("Index job succesfully finished.")
    spark.stop()
  }

  /**
    * Add Column Index to dataframe to each row
    */
  def addColumnIndex(df: DataFrame, spark : SparkSession) = {
    spark.sqlContext.createDataFrame(
      df.rdd.zipWithIndex.map {
        case (row, index) => Row.fromSeq(row.toSeq :+ index)
      },
      // Create schema for index column
      StructType(df.schema.fields :+ StructField("index", LongType, false)))
  }
}