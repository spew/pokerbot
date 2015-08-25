package org.poker.poller;

import com.google.gson.Gson;
import org.jinstagram.auth.model.OAuthRequest;
import org.jinstagram.entity.common.InstagramErrorResponse;
import org.jinstagram.entity.oembed.OembedInformation;
import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.http.Response;
import org.jinstagram.http.Verbs;
import org.jinstagram.model.Constants;
import org.jinstagram.model.Methods;

import java.io.IOException;
import java.util.Map;

/*
 * this class fixes an issue where the base URL is incorrect in the public library
 */
public class InstagramOembedPatched {
  public OembedInformation getOembedInformation(String url) throws InstagramException {
    String apiMethod = String.format(Methods.OEMBED_INFORMATION, url);
    OembedInformation information = createInstagramObject(Verbs.GET, OembedInformation.class, apiMethod, null);
    return information;
  }

  private <T> T createInstagramObject(Verbs verbs, Class<T> clazz, String methodName, Map<String, String> params)
      throws InstagramException {
    Response response;
    try {
      response = getApiResponse(verbs, methodName, params);
    } catch (IOException e) {
      throw new InstagramException("IOException while retrieving data", e);
    }

    if (response.getCode() >= 200 && response.getCode() < 300) {
      return createObjectFromResponse(clazz, response.getBody());
    }

    throw handleInstagramError(response);
  }

  private InstagramException handleInstagramError(Response response) throws InstagramException {
    if (response.getCode() == 400) {
      Gson gson = new Gson();
      final InstagramErrorResponse error = InstagramErrorResponse.parse(gson, response.getBody());
      error.setHeaders(response.getHeaders());
      error.throwException();
    }
    throw new InstagramException("Unknown error response code: " + response.getCode() + " " + response.getBody(),
        response.getHeaders());
  }

  private Response getApiResponse(Verbs verb, String methodName, Map<String, String> params) throws IOException {
    Response response;
    String apiResourceUrl = "http://api.instagram.com/publicapi" + methodName;
    OAuthRequest request = new OAuthRequest(verb, apiResourceUrl);

    // Additional parameters in url
    if (params != null) {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        if (verb == Verbs.GET) {
          request.addQuerystringParameter(entry.getKey(), entry.getValue());
        } else {
          request.addBodyParameter(entry.getKey(), entry.getValue());
        }
      }
    }

    response = request.send();

    return response;
  }

  private <T> T createObjectFromResponse(Class<T> clazz, final String response) throws InstagramException {
    Gson gson = new Gson();
    T object;

    try {
      object = gson.fromJson(response, clazz);
    } catch (Exception e) {
      throw new InstagramException("Error parsing json to object type " + clazz.getName(), e);
    }

    return object;
  }
}
