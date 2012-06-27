package me.battleship.util;

/**
 * Methods for handling strings
 *
 * @author Manuel Vögele
 */
public class StringUtils
{
	/**
	 * Returns the substring between two separators
	 * @param string the string
	 * @param separator1 the first separator
	 * @param separator2 the second separator
	 * @return the substring between the two separators
	 */
	public static String substringBetween(String string, String separator1, String separator2) 
	{
		int sep1pos = string.indexOf(separator1) + separator1.length();
		return string.substring(sep1pos, string.indexOf(separator2, sep1pos));
	}
	
	/**
	 * Returns a attribute from a string containing a json file
	 * @param json the string containing the json file
	 * @param attribute the attribute
	 * @return the value of the attribute
	 */
	public static String readAttributeFromJson(String json, String attribute)
	{
		return substringBetween(json, attribute + "\":\"", "\"");
	}
}
