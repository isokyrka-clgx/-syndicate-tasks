package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

@LambdaHandler(lambdaName = "sqs_handler",
		roleName = "sqs_handler-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}"
)
@SqsTriggerEventSource(
		targetQueue = "async_queue",
		batchSize = 1
)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	@Override
	public Void handleRequest(SQSEvent event, Context context) {
		for(SQSEvent.SQSMessage msg : event.getRecords()){
			System.out.println(new String(msg.getBody()));
		}
		return null;
	}
}
