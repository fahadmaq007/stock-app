package com.maqbool.stock.commons;


/**
 * Operation defines the restriction to be applied on the property. It closely works with {@link PropertySpec}
 * 
 * @author maqbool.ahmed
 * 
 */
public enum Operation {

  /**
   * Like '%'.
   */
  LIKE,
  /**
   * Equal.
   */
  EQ,
  /**
   * Less Than.
   */
  LT,

  /**
   * Greater Than.
   */
  GT,

  /**
   * Less Than or Equal.
   */
  LTE,

  /**
   * Greater Than or Equal.
   */
  GTE,

  /**
   * Not Equal.
   */
  NOTEQUAL,

  /**
   * Between Range usually for dates or timestamps.
   */
  BETWEEN,

  /**
   * Is Null?.
   */
  ISNULL,

  /**
   * Is Not Null?.
   */
  IS_NOT_NULL,
  /**
   * IN
   */
  IN,

  /**
   * AVG
   */
  AVG,

  /**
   * SUM
   */
  SUM,

  /**
   * NOT IN
   */
  NOT_IN,

}
