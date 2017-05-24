package pers.cmeu.common;

import java.util.List;

import pers.cmeu.models.AttributeCVF;

public class TableUtil {
	public static String getParmaryKeyType(String key,List<AttributeCVF> attr) {
		if (attr==null) {
			return  "Object";
		}
		for (AttributeCVF item : attr) {
			if (item.getConlumn().equalsIgnoreCase(key)) {
				return item.getJavaTypeValue();
			}
		}
		return "Object";
	}

}
