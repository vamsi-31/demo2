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
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		response.setHeaders(headers);

		// Get the path and method from the request
		String path = request.getPath();
		String httpMethod = request.getHttpMethod();

		// Check if the request is for /hello with GET method
		if ("/hello".equals(path) && "GET".equals(httpMethod)) {
			Map<String, Object> successBody = new HashMap<>();
			successBody.put("statusCode", 200);
			successBody.put("message", "Hello from Lambda");

			response.setStatusCode(200);
			response.setBody(convertMapToJson(successBody));
			return response;
		}

		// Handle all other requests as bad requests
		Map<String, Object> errorBody = new HashMap<>();
		errorBody.put("statusCode", 400);
		errorBody.put("message", String.format(
				"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
				path, httpMethod));

		response.setStatusCode(400);
		response.setBody(convertMapToJson(errorBody));
		return response;
	}

	private String convertMapToJson(Map<String, Object> map) {
		StringBuilder json = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (!first) {
				json.append(",");
			}
			json.append("\"").append(entry.getKey()).append("\":");
			if (entry.getValue() instanceof String) {
				json.append("\"").append(entry.getValue()).append("\"");
			} else {
				json.append(entry.getValue());
			}
			first = false;
		}
		json.append("}");
		return json.toString();
	}
}