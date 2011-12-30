package org.jquant.example;

import org.joda.time.DateTime;
import org.jquant.model.Currency;
import org.jquant.model.MarketDataPrecision;
import org.jquant.strategy.StrategyRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Main configuration class for the {@link StrategyRunner} component
 * @author patrick.merheb
 *
 */
@Configuration
public class StrategyRunnerConfiguration {

	
	@Bean
	public StrategyRunner runner(){
		
		DateTime debut = new DateTime("2005-03-25");
		DateTime fin = new DateTime();
		StrategyRunner runner = new StrategyRunner(debut,fin,Currency.EUR,MarketDataPrecision.CANDLE);
		runner.setBasePackage("org.jquant.example.ma");
		return runner;
	}
	
}
