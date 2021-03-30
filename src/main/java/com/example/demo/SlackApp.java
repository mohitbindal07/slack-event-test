package com.example.demo;

import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.bolt.App;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;


@Configuration
public class SlackApp {
	
	private static final Logger logger = LoggerFactory.getLogger(SlackApp.class);
	
	@Bean
	public App initSlackApp() {
		App app = new App();
		app.event(AppMentionEvent.class, (payload, ctx) -> {
			logger.info("app mention event executed with text value {} and type of event {}",
					payload.getEvent().getText(), payload.getEvent().getType());
			try {

				// Use the response.getBody()
				AppMentionEvent event = payload.getEvent();
				// Call the chat.postMessage method using the built-in WebClient
				String text = payload.getEvent().getText();
				if(text.contains("help")) {
					//return getHelp(event,ctx);
		    		ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
							// The token you used to initialize your app is stored in the `context` object
							.token(ctx.getBotToken())
							// Payload message should be posted in the channel where original message was
							// heard
							.channel(event.getChannel()).text("How can I help you today,\n"
				    				+ "1. Issue/Ticket tracking\n"
				    				+ "2. Work order status"));
		    	
		    		return ctx.ack();
		    	}
			}
				catch (IOException | SlackApiException e) {
					logger.error("hello error: {}", e.getMessage(), e);
				} 
				return ctx.ack();
			});
		
		
		String regex = "^[a-zA-Z0-9_.-|?#\\s]*$";
		Pattern pattern = Pattern.compile(regex);
		app.message(pattern, (req, ctx) -> {
			logger.info("message event pattern executed with text value {} and type of event {}",
					req.getEvent().getText(), req.getEvent().getType());
			
			try {
				MessageEvent event = req.getEvent();
				
				// Call the chat.postMessage method using the built-in WebClient
				String text = req.getEvent().getText();
				if(text.equals("help")){
					logger.info("message event pattern executed help typed");
					ChatPostMessageResponse result = ctx.client().chatPostMessage(r -> r
							// The token you used to initialize your app is stored in the `context` object
							.token(ctx.getBotToken())
							// Payload message should be posted in the channel where original message was
							// heard
							.channel(event.getChannel()).text("How can I help you today,\n"
				    				+ "1. Issue/Ticket tracking\n"
				    				+ "2. Work order status"));
				}
				
			} catch (IOException | SlackApiException e) {
				logger.error("hello error: {}", e.getMessage(), e);
			}
			return ctx.ack();
		});

		return app;
	
	}
}
	
