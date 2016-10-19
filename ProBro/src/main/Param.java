package main;

import java.io.Serializable;

/**
 * Parameter model class for simple, generically typed (name, value) pairs
 * 
 * @author xwebert
 *
 * @param <T> The generic type of the value
 */
public class Param<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the parameter
	 */
	private String name = null;
	
	/**
	 * Value of the parameter
	 */
	private T value = null;
	
	public Param(String name, T value) throws Throwable {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the parameter name
	 * 
	 * @return
	 */
	public String getName() throws Throwable {
		return name;
	}

	/**
	 * Returns the parameter value
	 * 
	 * @return
	 */
	public T getValue() throws Throwable {
		return value;
	}

	/**
	 * Set the parameter value
	 * 
	 * @param value
	 */
	public void setValue(T value) throws Throwable {
		this.value = value;
	}
	
	/**
	 * Two parameters are equal when they have the same name
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		try {
			return name.equals(((Param<T>)obj).name);
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
			return false;
		}
	}

	/**
	 * String output
	 * 
	 */
	@Override
	public String toString() {
		try {
			return getName() + " = "+ getValue();
			
		} catch (Throwable t) {
			Main.handleThrowable(t);
			return "";
		}
	}	
}
