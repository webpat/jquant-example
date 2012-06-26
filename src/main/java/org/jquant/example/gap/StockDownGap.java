package org.jquant.example.gap;

import java.util.Arrays;
import java.util.List;

import org.jquant.market.Instruments;
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
 * Same as SimpleGap except that we also requires that the previous candle was a down candle
 * @author patrick.merheb
 * 
 */
@Strategy
public class StockDownGap extends MonoAssetStrategy {

	@Parameter(category="parameters",description="quantity to trade")
	private final int qty = 100;

	@Parameter(category="parameters",description="Gap percent")
	private final double percent = 2;

	private double prevClose;

	private Order sellOrder;
	
	private boolean downDay = false;

	@Override
	public List<InstrumentId> getMarket() {
		return Arrays.asList(Instruments.HEINZ);
	
	}

	@Override
	public void init() {
		prevClose = -1;

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		prevClose = candle.getClose();
		
		downDay = candle.getClose() < candle.getOpen();
		
		if (hasPosition()) {
			if (sellOrder != null) {
				orderManager.cancelOrder(sellOrder);
			}
			sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.OPEN, "Exit");
		}

	}

	@Override
	public void onCandleOpen(InstrumentId instrument, Candle candle) {

		if (downDay && (prevClose - candle.getOpen()) / prevClose > percent / 100.0) {
			sendMarketOrder(instrument, OrderSide.BUY, qty, CandleData.OPEN, "Entry");

		}
	}

	@Override
	public void onPositionOpened(TradeSide side, InstrumentId instrument) {
		sellOrder = sendLimitOrder(instrument, OrderSide.SELL, qty, prevClose, "Limit Exit");
	}

}
