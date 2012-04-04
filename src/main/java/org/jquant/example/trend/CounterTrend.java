package org.jquant.example.trend;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.portfolio.Trade.TradeSide;
import org.jquant.serie.Candle;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

/**
 * Simple strategy that enters the market with limit orders
 * <p>
 * WARNING: the code is written just to handle <b>one</b> instrument in the strategy Market
 * @author patrick.merheb
 *
 */
@Strategy(value="Counter Trend strategy")
public class CounterTrend extends AbstractStrategy {

	
	private int mDownCount;
	private int mUpCount;
	private Candle mPrevCandle;
	// Simulation parameter (number of successive trend signals) 
	private int mNumSignal;
	
	@Override
	public void init() {
		mPrevCandle = new Candle(new DateTime(),Period.days(1),0,Double.MAX_VALUE, Double.MIN_VALUE,0,0);
		mNumSignal = 3;
	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		
		
		/*
		 * Update trend counters
		 */
		if (candle.getLow() < mPrevCandle.getLow()){
			mDownCount++;
		}else {
			mDownCount = 0;
		}
		
		if (candle.getHigh() > mPrevCandle.getHigh()){
			mUpCount++;
		}else {
			mUpCount = 0;
		}
		
		if (!hasPosition(instrument)){ // Am I flat ? 
			
			if (mDownCount == mNumSignal){
				// Expecting a mean reversing, Place limit Buy Order on Candle.Low + tickSize
				sendLimitOrder(instrument, OrderSide.BUY, 10, candle.getLow(), "Buy on loosing trend, expect a mean reversing");
				//Reset trend counter
				mDownCount = 0;
			}
			
			if (mUpCount == mNumSignal){
				// Expecting a mean reversing, Place limit Sell Order on Candle.High + tickSize
				sendLimitOrder(instrument, OrderSide.SELL, 10, candle.getHigh(), "Sell on a winning trend, expect a mean reversing");
				//Reset trend counter 
				mUpCount = 0;
			}
		}
		
		// warn: this line suppose there is only one instrument in the strategy market 
		mPrevCandle = candle;

	}

	@Override
	public void initMarket() {
		addInstrument(Instruments.GOOG);

	}
	
	@Override
	public void onPositionOpened(TradeSide side, InstrumentId instrumentId) {
		super.onPositionOpened(side, instrumentId);
		//SEND Trailing stop order for 10 googles high-low
		// if Position.LONG or Position.SHORT
	}

}
