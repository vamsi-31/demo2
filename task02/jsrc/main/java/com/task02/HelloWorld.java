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

	private static final String SUCCESS_RESPONSE = "{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}";
	private static final String ERROR_RESPONSE_TEMPLATE =
			"{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}";

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		if (request == null) {
			return createResponse(400, headers, String.format(ERROR_RESPONSE_TEMPLATE, "", ""));
		}

		String path = request.getPath();
		if (path == null) {
			path = "";
		}

		String httpMethod = request.getHttpMethod();
		if (httpMethod == null) {
			httpMethod = "";
		}

		// Handle /hello and subpaths like /hello/world
		if (path.startsWith("/hello") && "GET".equalsIgnoreCase(httpMethod)) {
			return createResponse(200, headers, SUCCESS_RESPONSE);
		}

		// For all other cases, return 400 Bad Request
		return createResponse(400, headers, String.format(ERROR_RESPONSE_TEMPLATE, path, httpMethod));
	}

	private APIGatewayProxyResponseEvent createResponse(int statusCode, Map<String, String> headers, String body) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(statusCode);
		response.setHeaders(headers);
		response.setBody(body);
		return response;
	}
}