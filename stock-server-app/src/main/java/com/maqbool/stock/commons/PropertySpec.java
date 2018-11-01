package com.maqbool.stock.commons;

import java.io.Serializable;


/**
 * PropertySpec defines the specifications on property. For eg. propertyName LIKE %, the propertyName is to be evaluated
 * with '%' by LIKE operation
 * 
 * @author maqbool.ahmed
 * 
 */
public class PropertySpec implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = -5631109974811514424L;

  private String propertyName;

  private Object value;

  private Operation operation;

  public PropertySpec() {
    this(null, null);
  }

  public PropertySpec(String propertyName, Object value) {
    this(propertyName, Operation.EQ, value);
  }

  public PropertySpec(String propertyName, Operation operation, Object value) {
    this.propertyName = propertyName;
    this.operation = operation;
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public String toString() {
    return Constants.OPEN_BRACKET + propertyName + "," + operation + ",value=" + value + Constants.CLOSE_BRACKET;
  }
}
