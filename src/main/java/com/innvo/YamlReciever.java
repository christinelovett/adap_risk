package com.innvo;

import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.innvo.web.rest.AttackTreeResource;

public class YamlReciever {
	
	public Object getData() throws IOException{
		AttackTreeResource receiver=new AttackTreeResource();
		     String fullfilename =URLDecoder.decode(receiver.getClass().getResource("/config/application-dev.yml").getFile(), "UTF-8");
		     YamlReader reader = new YamlReader(new FileReader(fullfilename));
		     Object fileContent = reader.read();
		     Map map = (Map) fileContent;
		     Object colors = map.get("colors");
		     return colors;
	}

}
