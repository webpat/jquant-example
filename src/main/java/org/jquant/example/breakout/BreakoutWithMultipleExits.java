package org.jquant.example.breakout;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

@Strategy
public class BreakoutWithMultipleExits extends AbstractStrategy {

	private final int ocaCount = 0;
	
	// Used for the exit
	private int barCount;
	
	private double highestHigh;
	
	private boolean entryEnabled = true;

	// simulation parameter : lookback period length 
	private int length;

	// simulation parameter: # bars holding the position 
	private int barsToExit;
	
	//simulation parameter 
	private int qty;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		if (entryEnabled){
			
			/*
			 * Don't trade until we accumulate enough bars in our series 
			 * for a proper look back of "length" bars into the past channel 
			 * we are trying to break out of  
			 */
			
			if (getCandleSerie(instrument).size()> length){
				
				/*
				 * if today's high is higher than all bars in the lookbak period of legth "length"
				 */
				if (candle.getHigh()>highestHigh){
					// then we are breaking out long, so issue a long side market buy 
					sendMarketOrder(instrument, OrderSide.BUY, qty, CandleData.OPEN, "Entry");
					// TODO : sell limit order for Take profit 
					// TODO: sell stop order for stop loss
					
				}
				
			}
		// Entry is disabled : We have an open position
		}else {
			/*
			 *  increment bar count while position is open
			 *  this count is used for the bar count exit method  
			 */
			barCount++;
			if (barCount > barsToExit){
				sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.OPEN, "Time Exit");
				// enable entry since we closed position
				entryEnabled = true;
			}
		}
		//TODO : HighestHigh indicator sur length 
		highestHigh = 0;

	}

	@Override
	public void initMarket() {
		addInstrument(Instruments.HEINZ);

	}

}
