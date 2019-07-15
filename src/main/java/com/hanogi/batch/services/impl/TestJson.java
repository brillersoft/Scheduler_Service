package com.hanogi.batch.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestJson {
	
	public static void main(String[] args) {
		
		String[] s1 = new String[] {"affection", "0.9118722081184387"};
		
		String[] s2 = new String[] {"joy", "0.8714778423309326"};
		
		List<String[]> ls = new ArrayList<>();
		
		ls.add(s1);
		
		ls.add(s2);
		
		String toneStr=null;
		
		for (String[] strings : ls) {
			
			
			if(null!=toneStr) {
				
				toneStr = String.join(",", toneStr, Arrays.toString(strings));
				
			}else {
				toneStr =  Arrays.toString(strings);
			}
			
			
		}
		
		System.out.println(toneStr);
		
		
		/*String jsonString = "\"{\"tones\": [[[\"affection\", 0.9118722081184387], [\"joy\", 0.8714778423309326]], [[\"affection\", 0.46921882033348083], [\"worry\", 0.3103422224521637]], [[\"affection\", 0.7272176146507263], [\"joy\", 0.6246799826622009], [\"neutral\", 0.5408819317817688]], [[\"affection\", 0.6294326186180115], [\"joy\", 0.5401041507720947], [\"worry\", 0.44770029187202454]], [[\"worry\", 0.40653127431869507]], [[\"joy\", 0.7357197999954224], [\"affection\", 0.4569379687309265]], [[\"affection\", 0.7750595808029175], [\"joy\", 0.6854126453399658], [\"worry\", 0.32013586163520813]], [[\"worry\", 0.4860808253288269]], [[\"neutral\", 0.49421635270118713]], [[\"affection\", 0.9336236119270325], [\"joy\", 0.8845330476760864]], [[\"joy\", 0.7221851348876953], [\"affection\", 0.6747922897338867], [\"neutral\", 0.536864697933197]], [[\"neutral\", 0.8073654770851135]], [[\"neutral\", 0.5869454145431519], [\"joy\", 0.31387051939964294]], [[\"neutral\", 0.8913936614990234]], [[\"neutral\", 0.8913936614990234]], [[\"neutral\", 0.8913936614990234]], [[\"neutral\", 0.8913936614990234]], [[\"neutral\", 0.8913936614990234]], [[\"neutral\", 0.6068903803825378], [\"joy\", 0.4234643578529358]], [[\"sadness\", 0.5349739193916321]]]}\"";
		
		String jsonStringNew = jsonString.replace("\"{","{").replace("}\"","}");
		System.out.println(jsonStringNew);
		
		JsonObject jsonObject = new JsonParser().parse(jsonStringNew).getAsJsonObject();
		*///ToneMapper toneData = gson.fromJson(responseBody, ToneMapper.class)
	}

}
