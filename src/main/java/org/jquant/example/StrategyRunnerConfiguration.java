package org.jquant.example;

import org.joda.time.DateTime;
import org.jquant.model.Currency;
import org.jquant.model.MarketDataPrecision;
import org.jquant.order.IOrderManager;
import org.jquant.order.OrderManager;
import org.jquant.portfolio.Portfolio;
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
	public IOrderManager broker(){
		
		IOrderManager broker = new OrderManager(0, 0, 0);
		return broker;
	}
	
	
	@Bean
	public StrategyRunner runner(){
		
		DateTime debut = new DateTime("2000-03-25");
		DateTime fin = new DateTime();
		

		Portfolio ptf = new Portfolio("My strategy portfolio", Currency.USD);
		/*
		 * Init Portfolio with 10 000 USD cash 
		 */
		ptf.addCash(10000);
		
		StrategyRunner runner = new StrategyRunner(ptf,debut,fin,Currency.EUR,MarketDataPrecision.CANDLE);
		runner.setBasePackage("org.jquant.example.trend");
		return runner;
	}
	
}
