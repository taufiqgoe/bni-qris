package id.taufiq.bniqris.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class BeanConfig {

    private final BniAttribute bniAttribute;

    public BeanConfig(BniAttribute bniAttribute) {
        this.bniAttribute = bniAttribute;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(bniAttribute.getBaseUrl())
                .defaultHeader("Content-Type", "application/json; charset=utf-8")
                .requestInterceptor(new LoggingInterceptor(bniAttribute.isLogRequest()))
                .build();
    }

    private static class LoggingInterceptor implements ClientHttpRequestInterceptor {

        private final boolean logRequest;

        private LoggingInterceptor(boolean logRequest) {
            this.logRequest = logRequest;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            response = new BufferingClientHttpResponseWrapper(response);

            if (logRequest) {
                logRequest(request, body);
                logResponse(response, body);
            }

            return response;
        }

        private void logRequest(HttpRequest request, byte[] body) {
            log.info("===========================request begin================================================");
            log.info("URI         : {}", request.getURI());
            log.info("Method      : {}", request.getMethod());
            log.info("Headers     : {}", request.getHeaders());
            if (isLoggableContentType(request.getHeaders().getContentType())) {
                log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
            } else {
                log.info("Request body: [binary or large content omitted]");
            }
            log.info("==========================request end================================================");
        }

        private void logResponse(ClientHttpResponse response, byte[] body) throws IOException {
            String responseBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            log.info("============================response begin==========================================");
            log.info("Status code  : {}", response.getStatusCode());
            log.info("Status text  : {}", response.getStatusText());
            log.info("Headers      : {}", response.getHeaders());

            if (responseBody.length() > 1000) {  // Log only first 1000 characters if body is large
                log.info("Response body: {}", responseBody.substring(0, 1000) + "[...]");
            } else {
                log.info("Response body: {}", responseBody);
            }
            log.info("=======================response end=================================================");
        }

        private boolean isLoggableContentType(org.springframework.http.MediaType mediaType) {
            if (mediaType == null) return false;
            return mediaType.includes(org.springframework.http.MediaType.APPLICATION_JSON)
                    || mediaType.includes(org.springframework.http.MediaType.TEXT_PLAIN)
                    || mediaType.includes(org.springframework.http.MediaType.APPLICATION_XML)
                    || mediaType.includes(org.springframework.http.MediaType.TEXT_XML);
        }

        public class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
            private final ClientHttpResponse response;
            private byte[] body;

            public BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
                this.response = response;
            }

            @Override
            public HttpStatusCode getStatusCode() throws IOException {
                return this.response.getStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return this.response.getStatusText();
            }

            @Override
            public void close() {
                this.response.close();
            }

            @Override
            public InputStream getBody() throws IOException {
                if (this.body == null) {
                    this.body = StreamUtils.copyToByteArray(this.response.getBody());
                }
                return new ByteArrayInputStream(this.body);
            }

            @Override
            public HttpHeaders getHeaders() {
                return this.response.getHeaders();
            }
        }
    }
}
