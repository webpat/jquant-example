package org.jquant.example.ma;

import org.jquant.data.Symbols;
import org.jquant.model.Candle;
import org.jquant.model.Symbol;
import org.jquant.strategy.AbstractStrategy;
import org.jquant.strategy.Strategy;

@Strategy(value="Simple Strategy")
public class SimpleStrategy extends AbstractStrategy {

	@Override
	public void init() {
		
		System.out.println("I've been called");

	}

	@Override
	public void createUniverse() {
		addInstrument(Symbols.ALCATEL);
		addInstrument(Symbols.MSFT);

	}

	@Override
	public void onCandle(Symbol instrument, Candle candle) {
		System.out.println("I've been called for Instrument " + instrument);
		
	}

}
