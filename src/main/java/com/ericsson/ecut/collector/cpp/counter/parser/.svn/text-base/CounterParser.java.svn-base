package com.ericsson.ecut.collector.cpp.counter.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ericsson.ecut.collector.cpp.counter.domain.MO;

public class CounterParser {
	private static Log logger = LogFactory.getLog("CounterParser");
	
	public static  Map<String, MO> parse(String input) throws Exception{
		Map<String, MO> mos = new HashMap<String, MO>();
		PrintoutStatus status = PrintoutStatus.before;
		
		String[] values = input.split("\n");
		
		for(String value:values){
			String[] headers = null;
			switch (status) {
				case before:
					if(value.startsWith("MO")){
						String[] tmpHeaders = value.split(" ");
						List<String> t = new ArrayList<String>();
						for(String header:tmpHeaders){
							if(!(header.equals("") || header.equals(" ")))
								t.add(header.trim());
						}
						headers = t.toArray(new String[0]);
						if(headers.length==3)
							status = PrintoutStatus.header;
						}
					break;
				case header:
					if(value.startsWith("==="))
						status = PrintoutStatus.counters;
					break;
				case counters:
					if(value.startsWith("=============="))
						status = PrintoutStatus.before;
					if(status != PrintoutStatus.before){
						List<String> t = new ArrayList<String>();
						String[] tmpHeaders = value.split(" ");
						for(String header:tmpHeaders){
							if(!(header.equals("") || header.equals(" ")))
								t.add(header.trim());
						}
						headers = t.toArray(new String[0]);
						if(headers.length == 3){
							String name = headers[0];
							//Integer id = null;
							//if(headers[0].contains("=")){
							//	name = headers[0].split("=")[0];
							//	id = Integer.parseInt(headers[0].split("=")[1]);
							//}
							if(!mos.containsKey(name))
								//if(id!=null)
								//	mos.put(name, new MO(name, id));
								//else
									mos.put(name, new MO(name));
							try{
								mos.get(name).addValue(headers[1], Double.parseDouble(headers[2]));
							}catch(Exception e){
								logger.error("Error parsing value", e);
							}
						}
					}
				default:
					break;
			}
			if(status == PrintoutStatus.done)
				break;
		}
		
		if(mos.size()==0)
			throw new Exception("No counters in output:" + input);
		return mos;
	}

	private enum PrintoutStatus{
		before,
		header,
		counters,
		done;
	}
}
