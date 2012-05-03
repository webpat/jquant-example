package org.jquant.example.pattern;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

/**
 * Four days up and Short for 1% Take profit 
 * @author patrick.merheb
 *
 */
@Strategy
public class FourDaysUpAndShortTakeProfit extends AbstractStrategy {

	//simultation parameter: quantity traded 
	private final int quantity = 10 ;
	
	//simulation parameter: Up Move
	private final double upPercent = 2;
	
	//simultation parameter : # of Consecutive Close Count  
	private final int ccc = 3 ;
	
	private int count;
	private double prevClose;
	
	
	@Override
	public void init() {
		prevClose = -1;
		count = 0;
		
	}

	/**
	 * FIXME : OnCandleOpen 
	 */
	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		if (prevClose != -1){
			if (!hasPosition(instrument)){
				if (prevClose < candle.getClose()){
					count++;
				}else {
					count = 0 ;
				}
				
				/*
				 * If we have seen 4 up days, and if the last day 
				 * up was 2% or more, then open a new position, going short on the day close 
				 */
				if (count == ccc){
					if ((((candle.getClose()-prevClose)/prevClose)*100)>=upPercent){
						sendMarketOrder(instrument, OrderSide.SELL, quantity, CandleData.OPEN, "4 days up && last day was 2% up  --> SELL on day 5 open");
					}
				}
				
			}else{
				sendMarketOrder(instrument, OrderSide.BUY, quantity, CandleData.OPEN,"day 6 Sell");
			}
		}
		prevClose = candle.getClose();
	}

	@Override
	public void initMarket() {
		addInstrument(Instruments.HEINZ);
		
	}

}
