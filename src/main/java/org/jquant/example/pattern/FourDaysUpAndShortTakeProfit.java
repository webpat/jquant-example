package org.jquant.example.pattern;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order;
import org.jquant.order.Order.OrderSide;
import org.jquant.portfolio.Trade.TradeSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoAssetStrategy;
import org.jquant.strategy.Parameter;
import org.jquant.strategy.Strategy;

/**
 * Four days up and Short for 1% Take profit 
 * @author patrick.merheb
 *
 */
@Strategy
public class FourDaysUpAndShortTakeProfit extends MonoAssetStrategy {

	@Parameter(category="parameters",description="quantity to trade")
	private final int quantity = 10 ;
	
	@Parameter(category="parameters",description="up percent move")
	private final double upPercent = 2;
	
	@Parameter(category="parameters",description="take profit in %")
	private final int takeProfit = 4;
	
	@Parameter(category="parameters",description="# of Consecutive Close Count")
	private final int ccc = 3 ;
	
	private int count;
	private double prevClose;

	private Order shortOrder;

	private Order tpOrder;
	
	
	@Override
	public void init() {
		prevClose = -1;
		count = 0;
		
	}
	
	
	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
	
		
		// We need to let a bar go by to capture the prev close
		if (prevClose != -1){
			if (!hasPosition()){
				if (prevClose < candle.getClose()){
					count++;
				}else {
					count = 0 ;
				}

				/*
				 * If we have seen 4 up days, and if the last day 
				 * up was 2% or more, then open a new position, going short on the day close 
				 * (eod market order, with a good broker and when volatility is low you should get an execution price near to close)
				 */
				if (count == ccc){
					if ((((candle.getClose()-prevClose)/prevClose)*100)>=upPercent){

						shortOrder = sendMarketOrder(instrument, OrderSide.SELL, quantity, CandleData.OPEN, "4 days up && last day was 2% up  --> SELL on day 5 open");
					}
				}


			}else{
				// Day 6 Get Out Of The Market anyhow 
				if (tpOrder != null){
					orderManager.cancelOrder(tpOrder);
				}
				sendMarketOrder(instrument, OrderSide.BUY, quantity, CandleData.OPEN,"day 6 Buy Cover");
			}
		}
		prevClose = candle.getClose();
	}
	
	@Override
	public void onPositionOpened(TradeSide side, InstrumentId instrumentId) {
		super.onPositionOpened(side, instrumentId);
		// Take Profit Limit Order
		double targetPrice = shortOrder.getFilledPrice() * (1-takeProfit/100.0);
		tpOrder = sendLimitOrder(instrumentId, OrderSide.BUY, quantity, targetPrice, "Exit (Take Profit)");
	}
	
	
	@Override
	public void initMarket() {
		addInstrument(Instruments.HEINZ);
	
	}

}
