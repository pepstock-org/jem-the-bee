package org.pepstock.jem.log;

import java.io.Serializable;


/**
 * Enumeration to be used as Message/Toast severity level
 * @author Marco "Fuzzo" Cuccato
 */
public enum MessageLevel implements Serializable {

	/**
	 * Used for normal messages
	 */
	INFO(0), 
	/** 
	 * Used for messages that needs attention
	 */
	WARNING(1),
	/**
	 * Used for error messages
	 */
	ERROR(2);
	
	private int intLevel = -1;
	
	private MessageLevel(int intLevel) {
		this.intLevel = intLevel;
	}
	
	/**
	 * @return the value of message level as int
	 */
	public int getIntLevel() {
		return intLevel;
	}
	
	/**
	 * @param intLevel
	 * @return the MessageLevel starting from int level value. Returns INFO by default.
	 */
	public static MessageLevel fromIntLevel(int intLevel) {
		MessageLevel parsed = null;
		switch (intLevel) {
			case 1:
				parsed = WARNING;
				break;
			case 2:
				parsed = ERROR;
				break;
			default:
				parsed = INFO;
				break;
		}
		return parsed;
	}
	
}
