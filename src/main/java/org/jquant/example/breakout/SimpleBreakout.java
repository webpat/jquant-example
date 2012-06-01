package org.jquant.example.breakout;

import java.util.Arrays;
import java.util.List;

import org.jquant.data.Instruments;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.MonoStrategy;
import org.jquant.strategy.Strategy;

/**
 * Simple Breakout with 4% Entry Limit
 * <p>
 * As each day begins, the strategy calculates an entry price 4% higher than yesterday's close, and issues a limit order to open a long position if that price is reached .
 * <p>
 * The next morning if a position exists, the strategy issue a market order to close the position
 * <p>
 * This strategy thinks that moving up 4% in a single day is such a big move that It would scare all the short sellers into covering their positions by buying long, thereby driving the price up even
 * further the next day.
 * 
 * @author patrick.merheb
 * 
 */
@Strategy
public class SimpleBreakout extends MonoStrategy {

	// simulation parameter
	private final int qty = 100;

	// simulation parameter
	private final double breakOutPercent = 4;

	private double prevClose;

	private Order buyOrder;

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

	}

	@Override
	public void onCandleOpen(InstrumentId instrument, Candle candle) {
		// Let the first candle go
		if (prevClose != -1) {

			if (!hasPosition()) {
				// Cancel yesterday limit order
				if (buyOrder != null) {
					orderManager.cancelOrder(buyOrder);
				}

				/*
				 * Set a buy stop order for a hypothetical 4% jump
				 */

				double breakoutPrice = prevClose * (1 + breakOutPercent / 100.0);
				buyOrder = sendStopOrder(instrument, OrderSide.BUY, qty, breakoutPrice, "Entry");

			} else {
				// Worst case scenario for the exit (sell on LOW)
				sendMarketOrder(instrument, OrderSide.SELL, qty, CandleData.LOW, "Exit");
			}

		}
	}

}
