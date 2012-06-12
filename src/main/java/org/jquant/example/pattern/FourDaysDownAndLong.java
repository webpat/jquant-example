package org.jquant.example.pattern;

import java.util.Arrays;
import java.util.List;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoStrategy;
import org.jquant.strategy.Strategy;

/**
 * Pattern matching strategy
 * <p>
 * CAUTION, only 1 instrument in the market 
 * @author patrick.merheb
 *
 */
@Strategy
public class FourDaysDownAndLong extends MonoStrategy {

	//simulation parameter: quantity traded 
	private final int quantity = 1000 ;
	
	//simulation parameter : number of down closes 
	private final int closesCount = 3 ;
	
	private int count;
	private double prevClose;
	
	
	@Override
	public void init() {
		prevClose = -1;
		count = 0;
		
	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		if (prevClose != -1){
			if (!hasPosition()){
				if (prevClose > candle.getClose()){
					count++;
				}else {
					count = 0 ;
				}
				
				/*
				 * If this is the fourth down day, issue a market order 
				 * to open long tomorrow morning on day 5 
				 */
				if (count == closesCount){
					sendMarketOrder(instrument, OrderSide.BUY, quantity, CandleData.OPEN, "4 days down --> BUY on day 5 open");
				}
				
			}else{
				sendMarketOrder(instrument, OrderSide.SELL, quantity, CandleData.OPEN,"day 6 Sell");
			}
		}
		prevClose = candle.getClose();
	}

	@Override
	public List<InstrumentId> getMarket() {
		return Arrays.asList(Instruments.EURUSD,Instruments.GBPUSD,Instruments.AUDUSD);
	
	}

}
