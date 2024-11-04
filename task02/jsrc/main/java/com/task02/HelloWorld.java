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
		// Set default headers
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		// Handle null request with a 400 response instead of 500
		if (request == null) {
			return createResponse(400, "Bad request: Request object is null", headers);
		}

		// Get path and method, defaulting to empty string if null
		String path = request.getPath() != null ? request.getPath() : "";
		String httpMethod = request.getHttpMethod() != null ? request.getHttpMethod() : "";

		// Log request details if context is available
		if (context != null) {
			context.getLogger().log("Request path: " + path);
			context.getLogger().log("HTTP method: " + httpMethod);
		}

		// Route requests based on path and method
		if ("/hello".equals(path) && "GET".equalsIgnoreCase(httpMethod)) {
			return createResponse(200, "Hello from Lambda", headers);
		} else if (path.startsWith("/cmtr-") && "GET".equalsIgnoreCase(httpMethod)) {
			return createResponse(400, String.format(
					"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
					path, httpMethod
			), headers);
		}

		// Default case for unmatched routes
		return createResponse(400, String.format(
				"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
				path, httpMethod
		), headers);
	}

	private APIGatewayProxyResponseEvent createResponse(int statusCode, String message, Map<String, String> headers) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setHeaders(headers);
		response.setStatusCode(statusCode);
		response.setBody(String.format("{\"statusCode\": %d, \"message\": \"%s\"}", statusCode, message));
		return response;
	}
}