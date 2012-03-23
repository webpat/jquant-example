package org.jquant.example.ma;

import java.util.HashMap;
import java.util.Map;

import org.jquant.data.Symbols;
import org.jquant.indicator.SMA;
import org.jquant.instrument.Equity;
import org.jquant.model.IInstrument;
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
public class SimpleStrategy extends AbstractStrategy {

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
		addInstrument(Symbols.HEINZ);
		addInstrument(Symbols.IBM);

	}

	@Override
	public void onCandle(InstrumentId instrument, Candle candle) {
		
		IInstrument i = new Equity(instrument);
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
				sendMarketOrder(i, OrderSide.BUY, qty, "Achat sur signal SMA");
				logger.info("Bought "+qty + " of " + instrument.getCode() + " at date " + candle.getDate().toString("dd/MM/yyyy"));
			}
		}
		/*
		 * If Open moves below SMA then it's a SELL signal, GET OUT of the Market 
		 */
		if (smaList.get(instrument).getValue(candle.getDate())<candle.getOpen()){
			// Do we have a position on instrument i ? 
			if (portfolio.getPosition(i)>0){
				
				double qty = portfolio.getPosition(i);
				// Sell on low (worst case) 
				sendMarketOrder(i, OrderSide.SELL, qty, "Vente sur signal SMA");
				logger.info("Sold "+qty + " of " + instrument.getCode()+ " at date " + candle.getDate().toString("dd/MM/yyyy"));
			}
		}
		
		
	}

}
