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

		// Ensure request is not null
		if (request == null) {
			return createErrorResponse("", "", headers);
		}

		// Get path and method, ensuring they're not null
		String path = request.getPath() != null ? request.getPath() : "";
		String httpMethod = request.getHttpMethod() != null ? request.getHttpMethod() : "";

		// Log request details
		if (context != null) {
			context.getLogger().log("Request path: " + path);
			context.getLogger().log("HTTP method: " + httpMethod);
		}

		// Handle /hello endpoint
		if ("/hello".equals(path) && "GET".equalsIgnoreCase(httpMethod)) {
			return createSuccessResponse(headers);
		}

		// Handle all other paths with 400 error
		return createErrorResponse(path, httpMethod, headers);
	}

	private APIGatewayProxyResponseEvent createSuccessResponse(Map<String, String> headers) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);
		response.setHeaders(headers);
		response.setBody("{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}");
		return response;
	}

	private APIGatewayProxyResponseEvent createErrorResponse(String path, String httpMethod, Map<String, String> headers) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(400);
		response.setHeaders(headers);
		String errorMessage = String.format(
				"{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
				path, httpMethod
		);
		response.setBody(errorMessage);
		return response;
	}
}