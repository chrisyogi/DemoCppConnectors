package com.ericsson.ecut.collector.cpp.neal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import se.ericsson.cello.neal.Node;
import se.ericsson.cello.neal.NodeAccess;
import se.ericsson.cello.neal.cm.CmService;
import se.ericsson.cello.neal.cm.Mo;
import se.ericsson.cello.neal.cm.Struct;

import com.ericsson.ecut.collector.cpp.domain.ConfigurationVersion;
import com.ericsson.ecut.collector.cpp.util.DateUtil;
import com.ericsson.ecut.collector.exception.CommunicationException;
import com.ericsson.ecut.collector.exception.ConnectException;

public class MOCounterCollector {
	private String host;
	private Node node;
	private boolean connected;
	public MOCounterCollector(String host) {
		super();
		this.setHost(host);
		connected = false;
	}

	public void connect() throws ConnectException, UnknownHostException{
		try{
			node = NodeAccess.getNode(host);
			connected = true;
		} catch (UnknownHostException e){
			throw e;
		} catch (IOException e){
			throw new ConnectException("Could not connect to " + host, e);
		} catch (Exception e) {
			throw new ConnectException("Could not connect to " + host, e);

		}
	}
	
	public double getAttributeValue(String moString, String attribute) throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			try{
				mo = cs.getMo(moString);
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
			
			try{
				return mo.getAttribute(attribute, null).getIntValue();
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("No such attribute in Mo " + moString + ".", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	public double getAttributeValue(Mo mo, String attribute) throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{			
			try{
				return mo.getAttribute(attribute, null).getIntValue();
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("No such attribute in Mo " + mo.getType() + "=" + mo.getName() + ".", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	public long getAttributeValueLong(String moString, String attribute) throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			try{
				mo = cs.getMo(moString);
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
			
			try{
				return mo.getAttribute(attribute, null).getLongValue();
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("No such attribute in Mo " + moString + ".", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	public String getAttributeValueString(String moString, String attribute) throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			try{
				mo = cs.getMo(moString);
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
			
			try{
				return mo.getAttribute(attribute, null).getStringValue();
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("No such attribute in Mo " + moString + ".", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	public Set<String> getCellNames() throws CommunicationException{
		Set<String> cellsNames = new HashSet<String>();  
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			try{
				mo = cs.getMo("ManagedElement=1,ENodeBFunction=1");
				for(Mo m:mo.getChildren(null))
					if(m.getType().startsWith("EUtranCell"))
						cellsNames.add(m.getType() + "=" + m.getName());
						
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
			
			return cellsNames;
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	/**
	 * Get all children node of MO
	 * @param moName Full route to MO
	 * @return
	 * @throws CommunicationException
	 */
	public List<Mo> getSubMOs(String moName) throws CommunicationException{
		List<Mo> mos = new ArrayList<Mo>();  
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			try{
				mo = cs.getMo(moName);
				for(Mo m:mo.getChildren(null)){
					mos.add(m);
				}
						
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
			
			return mos;
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);

		}
	}
	
	/**
	 * Get all children from mO.
	 * @param moName MO without parents
	 * @param mos
	 * @return
	 */
	public List<Mo> getMOs(String moName, List<Mo> mos){
		List<Mo> returnMos = new ArrayList<Mo>();
		for(Mo m:mos){
			if(m.getType().equals(moName))
				returnMos.add(m);
		}
		return returnMos;
	}
	
	public List<Mo> getSubMOs(String moName, List<Mo> mos){
		List<Mo> returnMos = new ArrayList<Mo>();
		for(Mo mo:mos){
			for(Mo m:mo.getChildren(null))
				if(m.getType().equals(moName))
					returnMos.add(m);
		}
		return returnMos;
	}
	
	public List<Mo> getSubMOs(String moName, Mo mo){
		List<Mo> returnMos = new ArrayList<Mo>();
		for(Mo m:mo.getChildren(null)){
			if(m.getType().equals(moName))
				returnMos.add(m);
		}
		return returnMos;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public boolean isConnected() {
		return connected;
	}
	
	public static void main(String arg[]) throws ConnectException, CommunicationException, UnknownHostException{
		String ip = "lienb0514";
		if(arg.length>0)
			ip = arg[0];
		System.out.println("Connecting to: " + ip);
		MOCounterCollector counterCollector = new MOCounterCollector(ip);
		counterCollector.connect();
		System.out.println("Diff: "  + counterCollector.getHourDiff());
		Calendar cal = counterCollector.getTime();
		System.out.println(DateUtil.formatCalendar(cal));
		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, counterCollector.getHourDiff());
		System.out.println(DateUtil.formatCalendar(cal));

//		List<Mo> mos = counterCollector.getSubMOs("ManagedElement=1,NodeBFunction=1");
//		int nrRadioLinks = 0;
//
//		if(mos!=null)
//			mos = counterCollector.getSubMOs("Carrier", mos);
//		if(mos!=null)
//			mos = counterCollector.getSubMOs("RadioLinks", mos);
//		if(mos!=null)
//			for(Mo mo:mos)
//				nrRadioLinks += counterCollector.getAttributeValue(mo, "noOfRadioLinks");
//		
//		System.out.println("noOfRadioLinks: " + nrRadioLinks);
		
		
		
//		MOCounterCollector counterCollector = new MOCounterCollector("10.122.26.144");
//		//CPPTelnetClient telnet = new CPPTelnetClient("10.122.26.144", 23, 1000);
//		//telnet.open("test", "otenb5054");
//		
//		//counterCollector.connect();
//		
//		MOCounterCollector counterCollector2 = new MOCounterCollector("10.122.29.12");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println("Now: " + sdf.format(Calendar.getInstance().getTime()));
//		
//		counterCollector.connect();
//		counterCollector2.connect();
//		
//		//System.out.println(telnet.send("readclock"));
//		//telnet.close();
//		
//		Calendar cal1 =counterCollector.getTime();
//
//		
//		
//		System.out.println(cal1.get(Calendar.HOUR_OF_DAY) + ":" + cal1.get(Calendar.MINUTE));
//		//int diff = (int) ((cal1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())/(1000*60*60))-1;
//		System.out.println((Calendar.getInstance().getTimeInMillis() - cal1.getTimeInMillis()));
//		//System.out.println("Diff: " + diff);
//		System.out.println("Diff: " + counterCollector.getHourDiff());
//		
//		Calendar cal = Calendar.getInstance();
//		int i = 0;
//		while(cal.after(cal1.getTime())){
//			cal.add(Calendar.HOUR_OF_DAY, -1);
//			i--;
//		}
//		System.out.println("Cal: " + sdf.format(cal.getTime()));
//		System.out.println("i " + i);
//		//telnet = new CPPTelnetClient("10.122.29.12", 23, 1000);
//		//telnet.open("test", "otenb6003");
//		//System.out.println(telnet.send("readclock"));
//		cal1 = counterCollector2.getTime();
//		System.out.println(cal1.get(Calendar.HOUR_OF_DAY) + ":" + cal1.get(Calendar.MINUTE));
//		System.out.println((Calendar.getInstance().getTimeInMillis() - cal1.getTimeInMillis()));
//		//diff = (int) (cal1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())/(1000*60*60)-1;
//		System.out.println("Diff: " + counterCollector2.getHourDiff());
//		
//		cal = Calendar.getInstance();
//		i = 0;
//		
//		while(cal.after(cal1)){
//			cal.add(Calendar.HOUR_OF_DAY, -1);
//			i--;
//		}
//		System.out.println("i " + i);
//		System.out.println("Cal: " + sdf.format(cal.getTime()));
		//ConfigurationVersion cv = counterCollector.getLastCreatedCv();
		//System.out.println(cv.getName());
		//System.out.println(cv.getUpgradePackageId());
		//System.out.println(cv.getOperatorName());
		//System.out.println(cv.getCalendar());
		//System.out.println(counterCollector.getCellNames());
		//System.out.println(counterCollector.getAttributeValue("ManagedElement=1,MgwApplication=1", "pmNrOfMediaStreamChannelsBusy"));
//		long utc = counterCollector.getAttributeValueLong("ManagedElement=1,ManagedElementData=1", "nodeUTCTime");
//		Calendar cal = new GregorianCalendar();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		System.out.println(sdf.format(cal.getTime()));
//
//		cal.setTimeInMillis(utc-(1000*60*60));
//		System.out.println(sdf.format(cal.getTime()));
	}

	public String getTimeZoneString() throws CommunicationException{
		String t_timeZoneString= getAttributeValueString("ManagedElement=1,ManagedElementData=1", "nodeLocalTimeZone");
		return t_timeZoneString;		
	}
	
	public Calendar getTime() throws CommunicationException{
		long utc = getAttributeValueLong("ManagedElement=1,ManagedElementData=1", "nodeUTCTime");
		Calendar cal = Calendar.getInstance();//TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(utc);
		cal.add(Calendar.HOUR_OF_DAY,(TimeZone.getTimeZone("UTC").getRawOffset()-Calendar.getInstance().getTimeZone().getRawOffset())/(1000*60*60));
		//cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		return cal;
	}
	
	
	public int getHourDiff() throws CommunicationException{
		Calendar cal = getTime();
		//return (int) (cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())/(1000*60*60)-1;
		//System.out.println(Calendar.getInstance().getTimeInMillis() + " " + cal.getTimeInMillis() + " " + ((Calendar.getInstance().getTimeInMillis()-cal.getTimeInMillis())/(1000*60)));
		return DateUtil.getHourDiff( Calendar.getInstance(), cal);	
	}
	
	public ConfigurationVersion getExecutingCv() throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			ConfigurationVersion cv = null;
			try{
				mo = cs.getMo("ManagedElement=1,SwManagement=1,ConfigurationVersion=1");
				String executingCv = mo.getAttribute("executingCv", null).getStringValue();
				for(Struct struct:mo.getAttribute("storedConfigurationVersions", null).getStructArrayValue()){
					if(struct.getMember("name").getValue().equals(executingCv)){
						cv = new ConfigurationVersion();
						cv.setName(struct.getMember("name").getStringValue());
						cv.setIdentity(struct.getMember("identity").getStringValue());
						cv.setType(struct.getMember("type").getStringValue());
						cv.setUpgradePackageId(struct.getMember("upgradePackageId").getStringValue());
						cv.setOperatorName(struct.getMember("operatorName").getStringValue());
						cv.setOperatorComment(struct.getMember("operatorComment").getStringValue());
						cv.setDate(struct.getMember("date").getStringValue());
						cv.setStatus(struct.getMember("status").getStringValue());
					}
				}
				return cv;		
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);
		}
	}
	
	public ConfigurationVersion getLastCreatedCv() throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		try{
			CmService cs = node.getCmService();
			
			Mo mo = null;
			ConfigurationVersion cv = null;
			try{
				mo = cs.getMo("ManagedElement=1,SwManagement=1,ConfigurationVersion=1");
				String lastCreatedCv = mo.getAttribute("executingCv", null).getStringValue();
				for(Struct struct:mo.getAttribute("storedConfigurationVersions", null).getStructArrayValue()){
					if(struct.getMember("name").getValue().equals(lastCreatedCv)){
						cv = new ConfigurationVersion();
						cv.setName(struct.getMember("name").getStringValue());
						cv.setIdentity(struct.getMember("identity").getStringValue());
						cv.setType(struct.getMember("type").getStringValue());
						cv.setUpgradePackageId(struct.getMember("upgradePackageId").getStringValue());
						cv.setOperatorName(struct.getMember("operatorName").getStringValue());
						cv.setOperatorComment(struct.getMember("operatorComment").getStringValue());
						cv.setDate(struct.getMember("date").getStringValue());
						cv.setStatus(struct.getMember("status").getStringValue());
					}
				}
				return cv;		
			}catch(	se.ericsson.cello.neal.cm.CmException e){
				throw new CommunicationException("Mo not found.", e);
			}
		}catch(Exception e){
			throw new CommunicationException("Error communicationg with node " + host + " " + e.getMessage() + ".", e);
		}
	}
	
	public void listAllMO(){
		CmService cs = node.getCmService();
		traverseAndPrint(cs.getMo("ManagedElement=1"));
	}
	
	public void traverseAndPrint(Mo mo) {
		System.out.println(mo.getType() + "="  +mo.getName());
		if(mo.hasChildren(null)) {
			for(Mo m : mo.getChildren(null)) {
				traverseAndPrint(m, mo.getType() + "=" + mo.getName());
			}
		}
	}
	
	public void traverseAndPrint(Mo mo, String s) {
		System.out.println(s + "," + mo.getType() + "=" + mo.getName());
		if(mo.hasChildren(null)) {
			for(Mo m : mo.getChildren(null)) {
				traverseAndPrint(m, s + "," + mo.getType() + "=" + mo.getName());
			}
		}
	}
	
	public Mo findMo(String moName) throws CommunicationException{
		if(!connected)
			throw new CommunicationException("Not connected to host");
		
		Mo mo = node.getCmService().getRootMo();
		Mo foundMo = null;
		
		if(mo.hasChildren(null)) {
			for(Mo m : mo.getChildren(null)) {
				findMo(m, moName, foundMo);
			}
		}
		
		return foundMo;
	}
	
	private void findMo(Mo mo, String moName, Mo foundMo){
		if(foundMo!=null) return;
		
		if(mo.getType().equalsIgnoreCase(moName)){
			foundMo = mo;
			return;
		}
		
		if(mo.hasChildren(null)) {
			for(Mo m : mo.getChildren(null)) {
				findMo(m, moName, foundMo);
			}
		}
	}
	
	public void stop() {
		if(node!=null)
			node.notify();
	}
}
