package org.jquant.example.breakout;

import java.util.Arrays;
import java.util.List;

import org.jquant.data.Instruments;
import org.jquant.indicator.HighestHigh;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoStrategy;
import org.jquant.strategy.Strategy;

/**
 * Breakout on 30 bars 
 * <p>
 * Exit on :
 * <ol>
 * <li>Time method (10 bars) </li>
 * <li>TODO : Limit and Stop Orders OCA </li>
 * </ol>
 * @author patrick.merheb
 *
 */
@Strategy
public class BreakoutWithMultipleExits extends MonoStrategy {

	
	// Used for the exit
	private int barCount;
	
	private HighestHigh highestHigh;
	
	private boolean entryEnabled = true;

	// simulation parameter : lookback period length 
	private final int length = 30;

	// simulation parameter: # bars holding the position 
	private int barsToExit;
	
	//simulation parameter 
	private final int qty = 100;
	
	@Override
	public List<InstrumentId> getMarket() {
		return Arrays.asList(Instruments.HEINZ);
	
	}

	@Override
	public void init() {
		highestHigh = new HighestHigh(getSerie());

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		if (entryEnabled){
			
			/*
			 * Don't trade until we accumulate enough bars in our series 
			 * for a proper look back of "length" bars into the past channel 
			 * we are trying to break out of  
			 */
			
			if (getSerie().size()> length){
				
				/*
				 * if today's high is higher than all bars in the lookbak period of legth "length" (default is 30)
				 */
				if (candle.getHigh()==highestHigh.getValue(candle.getDate())){
					
					// then we are breaking out long, so issue a long side market buy for tomorrow 
					sendMarketOrder(instrument, OrderSide.BUY, qty, CandleData.OPEN, "Entry");
					
					entryEnabled = false;
					barCount = 0;
					
				}
				
			}
		// Entry is disabled : We have an open position
		}else {
			/*
			 *  increment bar count while position is open
			 *  this count is used for the bar count exit method  
			 */
			barCount++;
			
			/*
			 * If we want to exit the trade based on time, we use the bar count as a proxy for elapsed time
			 * and issue a market sell order 
			 */
			if (barCount > barsToExit){
				sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.CLOSE, "Time Exit");
				// enable entry since we closed position
				entryEnabled = true;
			}
		}
		

	}

	

}
