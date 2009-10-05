/*
* Copyright 2008-2009 Italian National Research Council
* 	All rights reserved
*/
package it.cnr.jada.util.format;

import it.cnr.jada.bulk.ValidationParseException;

/**
 * Formattatore di importi in EURO
 */
/**
 * 
 * @author mspasiano
 * @version 1.0
 * @since October 2009
 */
public class EuroPositivoFormat extends EuroFormat {

	private static final long serialVersionUID = 1L;

	public Object parseObject(String source) throws java.text.ParseException{
	
		Object obj = super.parseObject(source);
		if (obj != null && obj instanceof java.math.BigDecimal) {
			java.math.BigDecimal bd = (java.math.BigDecimal)obj;
			if (bd.signum() < 0)
				throw new ValidationParseException("sono ammessi solo valori positivi!", 0);
		}
		return obj;
		
	}
}