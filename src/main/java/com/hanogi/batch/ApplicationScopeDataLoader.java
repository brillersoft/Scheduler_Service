package com.hanogi.batch;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.hanogi.batch.constants.DeploymentTypes;
import com.hanogi.batch.constants.EmailServiceProviders;
import com.hanogi.batch.reader.IEmailReader;
import com.hanogi.batch.reader.OCMSExchangeReader;
import com.hanogi.batch.reader.OPMSExchangeReader;

@Component
public class ApplicationScopeDataLoader {

	@Autowired
	@Lazy
	private IEmailReader OPMSExchangeReader;
	
	@Autowired
	@Lazy
	private IEmailReader OCMSExchangeReader;

	public void loadMailReadersMap(Map<String, Map<String, IEmailReader>> mailReadersMap) {

		for (EmailServiceProviders serviceProviders : EmailServiceProviders.values()) {

			switch (serviceProviders) {

			case MicrosoftExchange:

				Map<String, IEmailReader> reader = new HashMap<>();

				reader.put(DeploymentTypes.OnPremise.name(), OPMSExchangeReader);
				reader.put(DeploymentTypes.OnCloud.name(), OCMSExchangeReader);

				mailReadersMap.put(EmailServiceProviders.MicrosoftExchange.name(), reader);

				break;

			default:
				break;
			}
		}

	}

}
