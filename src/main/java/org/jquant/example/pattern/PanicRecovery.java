package org.jquant.example.pattern;

import java.util.Arrays;
import java.util.List;

import org.jquant.market.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoAssetStrategy;
import org.jquant.strategy.Parameter;

public class PanicRecovery extends MonoAssetStrategy {

	@Parameter(category="parameters",description="buy when asset prices drop more than this % in one day")
	private final double percent = 5 ;

	@Parameter(category="parameters",description="quantity to trade")
	private final double qty = 100 ;

	private Order buyOrder; 

	@Override
	public void init() {
		

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		/*
		 *  If we do not have a position, update the limit buy order to be 
		 *  5% above today's close 
		 */
		if (!hasPosition()){
			if (buyOrder != null){
				orderManager.cancelOrder(buyOrder);
			}
			/*
			 * Issue a new limit buy order at percent (5%) below today's close 
			 * this order will execute tomorrow if limit is hit 
			 */
			double buyPrice = candle.getClose()*(1-(percent/100.0));
			buyOrder = sendLimitOrder(instrument, OrderSide.BUY, qty, buyPrice, "Entry");
			

		}else {
			/*
			 * else we opened a pos today (yesterday limit order was trigered sometime in the day), 
			 * we close our position at the end of today.
			 * We expect that such a big drop was freaky, and that prices recovered during the day. 
			 * If not, this order stops further losses. 
			 */
			sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.CLOSE, "Exit");
		}
	}

	@Override
	public List<InstrumentId> getMarket() {
		return Arrays.asList(Instruments.HEINZ);
	
	}


}
