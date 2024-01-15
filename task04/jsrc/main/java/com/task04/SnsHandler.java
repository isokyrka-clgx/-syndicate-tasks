package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

@LambdaHandler(lambdaName = "sns_handler",
		roleName = "sns_handler-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}"
)
@SnsEventSource(
		targetTopic = "lambda_topic"
)
public class SnsHandler implements RequestHandler<SNSEvent, Void> {

	public Void handleRequest(SNSEvent event, Context context) {
		for (SNSEvent.SNSRecord msg : event.getRecords()) {
			System.out.println(msg.getSNS().getMessage());
		}
		return null;
	}
}
