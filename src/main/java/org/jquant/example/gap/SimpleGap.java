package org.jquant.example.gap;

import java.util.Arrays;
import java.util.List;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order;
import org.jquant.order.Order.OrderSide;
import org.jquant.portfolio.Trade.TradeSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoStrategy;
import org.jquant.strategy.Strategy;

/**
 * This strategy watches for the stock to go down 2%, then buys the gap (long)
 * <p>
 * exit is on the next bar close or when the gap is closed (limit order)  
 * @author patrick.merheb
 * 
 */
@Strategy
public class SimpleGap extends MonoStrategy {

	// simulation parameter
	private final int qty = 100;

	// simulation parameter : Gap Percent
	private final double percent = 2;

	private double prevClose;

	private Order sellOrder;

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
		if (hasPosition()) {
			if (sellOrder != null) {
				orderManager.cancelOrder(sellOrder);
			}
			sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.CLOSE, "Exit");
		}

	}

	@Override
	public void onCandleOpen(InstrumentId instrument, Candle candle) {

		if ((prevClose - candle.getOpen()) / prevClose > percent / 100.0) {
			sendMarketOrder(instrument, OrderSide.BUY, qty, CandleData.OPEN, "Entry");

		}
	}

	@Override
	public void onPositionOpened(TradeSide side, InstrumentId instrument) {
		sellOrder = sendLimitOrder(instrument, OrderSide.SELL, qty, prevClose, "Limit Exit");
	}

}
