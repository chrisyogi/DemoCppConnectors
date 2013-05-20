package com.ericsson.ecut.collector.cpp.neal;

import java.io.IOException;
import java.net.UnknownHostException;

import se.ericsson.cello.neal.Node;
import se.ericsson.cello.neal.NodeAccess;
import se.ericsson.cello.neal.cm.CmService;
import se.ericsson.cello.neal.cm.Mo;

public class Tester {
	public void traverse(Mo mo) {
		// System.out.println(mo.getLdn());
		
		if(mo.hasChildren(null)) {
			for(Mo m : mo.getChildren(null)) {
				traverse(m);
			}
		}
	}
	
	public void sample() throws IOException {
		Node node = null;
		try{
			node = NodeAccess.getNode("mgw34");
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
		CmService cs = node.getCmService();
		
		Mo mo = cs.getMo("ManagedElement=1,MgwApplication=1");
		
		for(int i = 0; i < 10; i++) {
			System.out.println(mo.getAttribute("pmNrOfMediaStreamChannelsBusy", null).getIntValue());
		}
	}
	
	
	public void load() throws IOException {
		Node node = NodeAccess.getNode("10.75.0.7");
		
		CmService cs = node.getCmService();
		
		traverse(cs.getRootMo());
	}
	
	
	public static void main(String[] args) {
		Tester t = new Tester();
		
		try {
			long time = System.currentTimeMillis();
			t.sample();
			System.out.println("Duration = " + (System.currentTimeMillis() - time));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}