package com.ericsson.ecut.collector.cpp.helper;

import java.util.Set;

import com.ericsson.ecut.collector.cpp.log.domain.LogRecordBlock;
import com.ericsson.ecut.collector.sample.domain.Counter;
import com.ericsson.ecut.collector.sample.domain.CounterBlock;
import com.ericsson.ecut.collector.sample.domain.CounterBlocks;

public class LogFilesHelper {
	public static void addLogRecords(CounterBlocks counterBlocks, LogRecordBlock[]  logRecords, String resourceName, String resourceType){
		Set<CounterBlock> counters = counterBlocks.getCounterSet();
		for(LogRecordBlock logRecord:logRecords){

			if(!counters.contains(new CounterBlock(logRecord.getCalendar())))
				counters.add(new CounterBlock(logRecord.getCalendar()));
			
			CounterBlock counterBlock = counterBlocks.getCounterBlock(new CounterBlock(logRecord.getCalendar()));
			counterBlock.addCounter(new Counter(resourceName, resourceType, "settingRecords", (double) logRecord.getSettingRecords().size(),logRecord.getCalendar()));
			counterBlock.addCounter(new Counter(resourceName, resourceType, "shellRecords", (double) logRecord.getShellRecords().size(),logRecord.getCalendar()));
			counterBlock.addCounter(new Counter(resourceName, resourceType, "otherRecords", (double) logRecord.getOtherRecords().size(),logRecord.getCalendar()));
		}
	}
}
