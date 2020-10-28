import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.HttpMethod.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class HTTPMain {

    private static CloseableHttpClient httpSyncClient;

    public static CloseableHttpClient getSyncHttpClient() {
        return httpSyncClient;
    }

    public static void main(String[] args) {
        System.out.println(executeSyncAndGetResponse("GET","http://localhost:8080", null, null, null, null));
    }

    public static String executeSyncAndGetResponse(String method, String url,
                                                   List<Map.Entry<String, String>> queryParams, List<Map.Entry<String, String>> headers, String body,
                                                   CloseableHttpClient httpClient) {

        HttpUriRequest request;
        try {
            request = buildRequest(method, url, queryParams, headers, body);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return executeSync(request, httpClient);
    }



    public static String executeSync(HttpUriRequest request, CloseableHttpClient client) {
        CloseableHttpClient finalHttpClient = client != null ? client : getSyncHttpClient();
        try {
            CloseableHttpResponse response = finalHttpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpUriRequest buildRequest(String method, String url, List<Map.Entry<String, String>> queryParams,
                                              List<Map.Entry<String, String>> headers, String body) throws URISyntaxException {

        HttpRequestBase requestBase;
        String methodUpperCase = method.toUpperCase();

        switch (methodUpperCase) {
            case GET:
                requestBase = new HttpGet();
                break;
            case POST:
                requestBase = new HttpPost();
                break;
            case PUT:
                requestBase = new HttpPut();
                break;
            case DELETE:
                requestBase = new HttpDelete();
                break;
            default:
                throw new RuntimeException("Failed to buildRequest, unsupported methodUpperCase: " + methodUpperCase);
        }

        URIBuilder uriBuilder = new URIBuilder(url);
        if (isNotEmpty(queryParams)) {
            queryParams.forEach(queryParam -> uriBuilder.addParameter(queryParam.getKey(), queryParam.getValue()));
        }

        requestBase.setURI(uriBuilder.build());

        if (isNotEmpty(headers)) {
            headers.forEach(entry -> requestBase.setHeader(new BasicHeader(entry.getKey(), entry.getValue())));
        }

        if (StringUtils.isNotBlank(body) && (POST.equals(methodUpperCase) || PUT.equals(methodUpperCase))) {
            HttpEntityEnclosingRequestBase entityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) requestBase;
            HttpEntity httpEntity = new ByteArrayEntity(body.getBytes());
            entityEnclosingRequestBase.setEntity(httpEntity);
        }

        return requestBase;
    }

}