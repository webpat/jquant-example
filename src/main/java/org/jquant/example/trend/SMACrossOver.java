package org.jquant.example.trend;

import java.util.HashMap;
import java.util.Map;

import org.jquant.data.Instruments;
import org.jquant.indicator.SMA;
import org.jquant.model.InstrumentId;
import org.jquant.order.Order.OrderSide;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

/**
 * Deadly simple daily strategy, using a simple moving average 
 * 
 * @author patrick.merheb
 *
 */
@Strategy(value="Simple Strategy")
public class SMACrossOver extends AbstractStrategy {

	private static final int MA_LENGTH = 50;
	
	/**
	 * My indicators collection 
	 */
	Map<InstrumentId,SMA> smaList = new HashMap<InstrumentId,SMA>() ;
	
	@Override
	public void init() {
		
		/*
		 * Init Indicators 
		 */
		for (InstrumentId s : getMarket()){
			SMA sma = new SMA(getCandleSerie(s), MA_LENGTH, CandleData.CLOSE);
			smaList.put(s, sma);
		}
		
	}

	/**
	 * The Strategy universe
	 */
	@Override
	public void initMarket() {
		addInstrument(Instruments.HEINZ);
		addInstrument(Instruments.IBM);

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		
		
//		System.out.printf(	"name : %1$s \t " +
//							"date : %2$td-%2$tm-%2$tY \t " +
//							"close: %3$f \t" +
//							"sma :  %4$f \n"
//							, instrument,candle.getDate().toDate(), candle.getClose(),smaList.get(instrument).getValue(candle.getDate()) );
		
		/*
		 * If Open moves above SMA then it's a buy signal, ALL IN 
		 */
		if (smaList.get(instrument).getValue(candle.getDate())>candle.getOpen()){
			// Do we have cash 
			if (portfolio.getCash()>candle.getHigh()){
				
				double qty = Math.floor(portfolio.getCash()/candle.getClose());
				sendMarketOrder(instrument, OrderSide.BUY, qty,CandleData.OPEN,  "Achat sur signal SMA");
				logger.info("Bought "+qty + " of " + instrument.getCode() + " at date " + candle.getDate().toString("dd/MM/yyyy"));
			}
		}
		/*
		 * If Open moves below SMA then it's a SELL signal, GET OUT of the Market 
		 */
		if (smaList.get(instrument).getValue(candle.getDate())<candle.getOpen()){
			// Do we have a position on instrument i ? 
			if (hasPosition(instrument)){
				
				double qty = portfolio.getPosition(instrument);
				// Sell 
				sendMarketOrder(instrument, OrderSide.SELL, qty,CandleData.OPEN, "Vente sur signal SMA");
				logger.info("Sold "+qty + " of " + instrument.getCode()+ " at date " + candle.getDate().toString("dd/MM/yyyy"));
			}
		}
		
		
	}

}