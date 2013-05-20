package com.ericsson.ecut.collector.cpp.counter.domain;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.ericsson.ecut.collector.cpp.log.domain.LogRecordBlock;
import com.ericsson.ecut.collector.sample.domain.CounterBlock;
import com.ericsson.ecut.collector.sample.domain.CounterBlocks;
import com.ericsson.ecut.collector.sample.domain.TimeStep;

public class CounterLogBlocks {
	private TimeStep step;
	private Set<CounterLogBlock> counters;
	
	public CounterLogBlocks(TimeStep step){
		this.setStep(step);
		counters  = new LinkedHashSet<CounterLogBlock>();
	}
	
	public CounterLogBlocks addCounterBlocks(CounterBlocks counterBlocks){
		for(Iterator<CounterBlock> it = counterBlocks.getCounterSet().iterator(); it.hasNext();){
			CounterBlock counter = it.next();
			if(!counters.contains(new CounterLogBlock(counter.getCalendar())))
				counters.add(new CounterLogBlock(counter.getCalendar()));
			
			getCounterLogBlock(new CounterLogBlock(counter.getCalendar())).setCounters(counter);
		}
		return this;
	}
	
	public CounterLogBlocks addLogRecordBlocks(LogRecordBlock[] logRecordBlocks){
		for(LogRecordBlock logRecordBlock:logRecordBlocks){
			if(!counters.contains(new CounterLogBlock(logRecordBlock.getCalendar())))
				counters.add(new CounterLogBlock(logRecordBlock.getCalendar()));
			
			CounterLogBlock counterLogBlock = getCounterLogBlock(new CounterLogBlock(logRecordBlock.getCalendar()));
			counterLogBlock.setNrOtherRecords(logRecordBlock.getOtherRecords().size());
			counterLogBlock.setNrShellRecords(logRecordBlock.getShellRecords().size());
			counterLogBlock.setNrSettingRecords(logRecordBlock.getSettingRecords().size());
		}
		return this;
	}
	
	public CounterLogBlock getCounterLogBlock(CounterLogBlock element) {
	    for (Iterator<CounterLogBlock> it = counters.iterator(); it.hasNext();) {
	    	CounterLogBlock current = it.next();
	        if (current.equals(element)) {
	        	return current;
	        }
		}
		return null;
	}
	
	public void setStep(TimeStep step) {
		this.step = step;
	}
	public TimeStep getStep() {
		return step;
	}
	public void setCounterLogBlocks(Set<CounterLogBlock> counters) {
		this.counters = counters;
	}
	public Set<CounterLogBlock> getCounterLogBlocks() {
		return counters;
	}

}
