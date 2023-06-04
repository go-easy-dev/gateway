package go.easy.gateway.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class TokenValidationClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8090")
            .build();

    public boolean validateToken(ValidateTokenRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        return webClient.post()
                .uri("api/v1/auth/token/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class)
                .toFuture()
                .get(20L, TimeUnit.SECONDS)
                .isValid();
    }
}

