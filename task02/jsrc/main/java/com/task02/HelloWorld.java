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
		// Initialize response objects
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		response.setHeaders(headers);

		// Handle null request
		if (request == null) {
			return createErrorResponse(500, "Internal server error: null request");
		}

		// Log request details if context is available
		if (context != null) {
			context.getLogger().log("Request path: " + (request.getPath() != null ? request.getPath() : "null"));
			context.getLogger().log("HTTP method: " + (request.getHttpMethod() != null ? request.getHttpMethod() : "null"));
		}

		String path = request.getPath();
		String httpMethod = request.getHttpMethod();

		// Validate required request parameters
		if (httpMethod == null || path == null) {
			return createErrorResponse(400, String.format(
					"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
					path != null ? path : "null",
					httpMethod != null ? httpMethod : "null"
			));
		}

		// Route requests based on path and method
		if (path.equals("/hello") && httpMethod.equalsIgnoreCase("GET")) {
			return createResponse(200, "Hello from Lambda");
		} else if (path.startsWith("/cmtr-") && httpMethod.equalsIgnoreCase("GET")) {
			return createErrorResponse(400, String.format(
					"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
					path, httpMethod
			));
		}

		// Default case for unmatched routes
		return createErrorResponse(400, String.format(
				"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
				path, httpMethod
		));
	}

	private APIGatewayProxyResponseEvent createResponse(int statusCode, String message) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		response.setHeaders(headers);
		response.setStatusCode(statusCode);
		response.setBody(String.format("{\"statusCode\": %d, \"message\": \"%s\"}", statusCode, message));
		return response;
	}

	private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
		return createResponse(statusCode, message);
	}
}