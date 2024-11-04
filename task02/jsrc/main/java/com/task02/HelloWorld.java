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
public class HelloWorld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		// Create common headers
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Access-Control-Allow-Origin", "*");

		String path = request.getPath();
		String httpMethod = request.getHttpMethod();

		// Check if the request is for /hello endpoint with GET method
		if ("/hello".equals(path) && "GET".equalsIgnoreCase(httpMethod)) {
			// Return successful response for /hello GET request
			String successBody = "{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}";
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(200)
					.withHeaders(headers)
					.withBody(successBody);
		} else {
			// Return 400 Bad Request for any other endpoint or method
			String errorMessage = String.format(
					"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
					path,
					httpMethod
			);
			String errorBody = String.format(
					"{\"statusCode\": 400, \"message\": \"%s\"}",
					errorMessage
			);
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(400)
					.withHeaders(headers)
					.withBody(errorBody);
		}
	}
}