/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.github.deaktator.testing.p1;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface Protocol1 {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"Protocol1\",\"namespace\":\"testing.p1\",\"types\":[{\"type\":\"record\",\"name\":\"ScoreE\",\"fields\":[{\"name\":\"value\",\"type\":[{\"type\":\"array\",\"items\":\"int\"},\"null\"],\"default\":[]}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  public interface Callback extends Protocol1 {
    public static final org.apache.avro.Protocol PROTOCOL = Protocol1.PROTOCOL;
  }
}