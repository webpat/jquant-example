package org.jquant.example.ma;

import java.util.HashMap;
import java.util.Map;

import org.jquant.data.Symbols;
import org.jquant.indicator.SMA;
import org.jquant.model.Symbol;
import org.jquant.serie.Candle;
import org.jquant.serie.Candle.CandleData;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

@Strategy(value="Simple Strategy")
public class SimpleStrategy extends AbstractStrategy {

	private static final int MA_LENGTH = 50;
	
	/**
	 * My indicators collection 
	 */
	Map<Symbol,SMA> smaList = new HashMap<Symbol,SMA>() ;
	
	
	@Override
	public void init() {
		
		/**
		 * Init Indicators 
		 */
		for (Symbol s : getUniverse()){
			SMA sma = new SMA(getCandleSerie(s), MA_LENGTH, CandleData.CLOSE);
			smaList.put(s, sma);
		}
		

	}

	/**
	 * The Strategy universe
	 */
	@Override
	public void createUniverse() {
		addInstrument(Symbols.HEINZ);
		addInstrument(Symbols.IBM);

	}

	@Override
	public void onCandle(Symbol instrument, Candle candle) {
		
		//logger.debug("I've been called for Instrument " + instrument + " with candle " + candle + " at " + candle.getDate());
		
		System.out.printf(	"name : %1$s \t " +
							"date : %2$td-%2$tm-%2$tY \t " +
							"close: %3$f \t" +
							"sma :  %4$f \n"
							
							, instrument,candle.getDate().toDate(), candle.getClose(),smaList.get(instrument).getValue(candle.getDate()) );
		
		
	}

}
