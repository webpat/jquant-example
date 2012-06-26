package org.jquant.example.trend;

import java.util.Arrays;
import java.util.List;

import org.jquant.indicator.SMA;
import org.jquant.market.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order;
import org.jquant.strategy.AbstractStrategy;

public class SMACrossOver extends AbstractStrategy {

	// simulation parameter : stop level
	double stopLevel = 0.05;
	
	// simulation parameter : stop level
	double limitOCALevel = 1.05;
	
	// simulation parameter : stop level
	double stopOCALevel = 1.05;
	
	// simulation parameter : Quantity
	double quantity = 100;
	
	/*
	 * Orders used in the strategy 
	 */
	Order marketOrder, limitOrder, stopOrder;
	
	/*
	 * Fast and Slow Moving averages 
	 */
	SMA sma1,sma2;
	
	// One position at a time 
	boolean entryEnabled;
	
	
	
	@Override
	public void init() {
		
	}

	@Override
	public List<InstrumentId> getMarket() {
		return Arrays.asList(Instruments.HEINZ);
	
	}

}
