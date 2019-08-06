package com.ljunggren.common.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import com.jcraft.jsch.JSchException;

public class JSONUtils {

    public static String objectToJson(Object object) {
        String json = new String();
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(object);
        }
        catch (IOException e) {
            json = null;
        }
        return json;
    }
    
	public static String prettyPrintObjectToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }
        catch (IOException e) {
            return null;
        }
	}
	
	public static String prettyPrintStringToJson(String string) {
        ObjectMapper mapper = new ObjectMapper();
        try {
        	JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(string);
        	JsonNode jsonNode = mapper.readTree(parser);
        	Object object = mapper.readValue(jsonNode.toString(), Object.class);
        	return prettyPrintObjectToJson(object);
        }
        catch (IOException e) {
            return null;
        }
	}
	
    public static <T> T jsonToObject(JSONObject json, Class<T> clazz) {
    	try {
    		ObjectMapper mapper = new ObjectMapper();
 			return mapper.readValue(json.toString(), clazz);
		} catch (IOException e) {
			System.out.println(ExceptionUtils.getMessage(e));
		}
    	return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object jsonToObject(String json, Class clazz) {
    	ObjectMapper mapper = new ObjectMapper();
        try {
			return mapper.readValue(json, clazz);
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }

	@SuppressWarnings("rawtypes")
	public static Object jsonToListObject(String json, Class clazz) {
    	ObjectMapper mapper = new ObjectMapper();
    	JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        try {
			return mapper.readValue(json, type);
		} 
        catch (IOException e) {
			// add logging
		}
        return null;
    }
	
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
    	try {
			ObjectMapper mapper = new ObjectMapper();
			jsonMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() { });
			return jsonMap;
    	}
    	catch (Exception e) {
    		jsonMap.put("error", ExceptionUtils.getMessage(e));
    	}
    	return jsonMap;
	}

    public static String errorMessageToJson(String errorMessage) {
        return objectToJson(createErrorMap(errorMessage));
    }

    public static String exceptionToJson(Exception e) {
    	if (e == null) {
    		return errorMessageToJson("Null Exception");
    	}
        return objectToJson(createErrorMap(e.getMessage()));
    }

    public static String JSchExceptionToJson(JSchException jsche) {
    	if (jsche == null) {
    		return errorMessageToJson("Null JSch Exception");
    	}
        if (jsche.getCause() != null) {
            String[] cause = jsche.getCause().toString().split(":");
            if (cause.length > 1) {
                if (cause[0].trim().equals("java.net.UnknownHostException")) {
                    return errorMessageToJson("Unknown Host: " + cause[1].trim());
                }
            }
        }
        else if (jsche.getMessage() != null) {
            if (jsche.getMessage().trim().equals("Auth fail")) {
                return errorMessageToJson("Authentication Failure");
            }
        }
        return exceptionToJson(jsche);
    }

    private static Map<String, String> createErrorMap(String errorMessage) {
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("error", errorMessage);
        return errorMap;
    }

    public static boolean isValidJSON(String json) {
        if (json == null) {
        	return false;
        }
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
            while (parser.nextToken() != null) { }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
}
