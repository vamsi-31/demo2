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
		if (context != null) {
			context.getLogger().log("Request path: " + (request.getPath() != null ? request.getPath() : "null"));
			context.getLogger().log("HTTP method: " + (request.getHttpMethod() != null ? request.getHttpMethod() : "null"));
		}

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		response.setHeaders(headers);

		if (request == null) {
			response.setStatusCode(500);
			response.setBody("{\"statusCode\": 500, \"message\": \"Internal server error: null request\"}");
			return response;
		}

		String path = request.getPath();
		String httpMethod = request.getHttpMethod();


		if (path != null && path.startsWith("/hello") && "GET".equalsIgnoreCase(httpMethod)) {
			response.setStatusCode(200);
			response.setBody("{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}");
		}

		else if (path != null && path.startsWith("/cmtr-") && "GET".equalsIgnoreCase(httpMethod)) {
			response.setStatusCode(400);
			response.setBody(String.format(
					"{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
					path, httpMethod
			));
		}

		else {
			response.setStatusCode(400);
			response.setBody(String.format(
					"{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
					path != null ? path : "null",
					httpMethod != null ? httpMethod : "null"
			));
		}

		return response;
	}
}