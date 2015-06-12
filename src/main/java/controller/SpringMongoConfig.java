package controller;

import com.mongodb.Mongo;

import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.ServerAddress;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;

import java.util.Arrays;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

	@Override
	public String getDatabaseName() {
		return "cmpe275";
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient("ds061751.mongolab.com:61751");
	}
}
