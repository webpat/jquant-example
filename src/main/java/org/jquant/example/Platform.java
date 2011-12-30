package org.jquant.example;

import org.jquant.strategy.StrategyRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Platform {

	@Autowired
	StrategyRunner runner;
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("jquant-config.xml");

		Platform p = context.getBean(Platform.class);
		p.start();
		
	}

	private void start() {
		runner.run();
	}

}
