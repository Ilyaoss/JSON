package ru.miet.json;

import java.io.PrintWriter;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONthread extends Thread{
	JsonParser parserGSON;
	final PrintWriter outMessage;
	final String message;
	final int libType;
	final static int LIBS_COUNT = 3;
	public JSONthread(int libType, String message, PrintWriter outMessage){
		this.outMessage = outMessage;
		this.message = message;
		this.libType = libType;
	}
	@Override
	public void run() {
		switch(libType % LIBS_COUNT){
		case 0:{
			try {
					new JsonParser().parse(message).getAsJsonObject();
			}
			catch(JsonParseException ex ) {
				synchronized(outMessage){
					outMessage.write("{\"message\":\"parsing error\"}");//json format exception sending
				}
				
			}
		}
		case 1:{
			try{
				new org.json.JSONObject(message);
			}
			catch(JSONException ex){
				synchronized(outMessage){
					outMessage.write("{\"message\":\"parsing error\"}");//json format exception sending
				}
			}
		}
		case 2:{
			try{
				new JSONParser().parse(message);
			}
			catch(ParseException ex){
				synchronized(outMessage){
					outMessage.write("{\"message\":\"parsing error\"}");//json format exception sending
				}
			}
		}
		}
	}
		
}

