package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
		lambdaName = "hello_world",
		roleName = "hello_world-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {

		// Extract request details from the payload
		Map<String, Object> requestContext = (Map<String, Object>) request.get("requestContext");
		Map<String, Object> http = (Map<String, Object>) requestContext.get("http");

		// Get the path and method
		String path = (String) http.get("path");
		String httpMethod = (String) http.get("method");

		Map<String, Object> response = new HashMap<>();

		// Check if the request is for /hello
		if ("/hello".equals(path)) {
			response.put("statusCode", 200);
			response.put("body", "{" +
					"\"statusCode\": 200," +
					"\"message\": \"Hello from Lambda\"" +
					"}");
		} else {
			response.put("statusCode", 400);
			response.put("body", "{" +
					"\"statusCode\": 400," +
					"\"message\": \"Bad request syntax or unsupported method. Request path: " + path + ". HTTP method: " + httpMethod + "\"" +
					"}");
		}

		return response;
	}
}