package com.example.demo;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {
	Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
			SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return new CommandLineRunner() {
			@SuppressWarnings("unchecked")
			@Override
			public void run(String... args) throws Exception {
				String commandlineArguments = null;
				for (int i = 0; i < args.length; i++) {
					commandlineArguments = args[i];
				}
				try {
				String fooResourceUrl = "https://api.weather.gov/points/" + commandlineArguments;// 39.7456,-97.0892";
				logger.info("fooResourceUrl : " + fooResourceUrl);
				ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
				//logger.info("Argument : " + response.getBody());
				

				JSONParser parser = new JSONParser(response.getBody());
				LinkedHashMap<String, Object> jsonMap = parser.parseObject();
				String gridIdValue = null;
				BigInteger gridXValue = null, gridYValue = null;
				for (Entry<String, Object> entry : jsonMap.entrySet()) {

					if ("properties".equalsIgnoreCase(entry.getKey())) {
						for (Entry<String, Object> entry1 : ((LinkedHashMap<String, Object>) entry.getValue())
								.entrySet()) {

							if ("gridId".equalsIgnoreCase(entry1.getKey())) {
								gridIdValue = (String) entry1.getValue();
								logger.info("gridIdValue : " + gridIdValue);
							}

							if ("gridX".equalsIgnoreCase(entry1.getKey())) {
								gridXValue = (BigInteger) entry1.getValue();
								logger.info("gridXValue : " + gridXValue);
							}

							if ("gridY".equalsIgnoreCase(entry1.getKey())) {
								gridYValue = (BigInteger) entry1.getValue();
								logger.info("gridYValue : " + gridYValue);
							}
						}
					}

				}

				String forcastrl = "https://api.weather.gov/gridpoints/" + gridIdValue + "/" + gridXValue + ","
						+ gridYValue + "/forecast/";// TOP/31,80/forecast/
				logger.info("forcastrl forcastrl : " + forcastrl);
				ResponseEntity<String> responseForcast = restTemplate.getForEntity(forcastrl, String.class);
				logger.info("responseForcast : " + responseForcast.getBody());
				} catch (IllegalStateException ise) {
					logger.info("Exception : " + ise.getMessage());
				} catch (Exception ex) {
					logger.info("Exception : " + ex.getMessage());
				}
			}
		};
	}

}
