package go.easy.gateway.filter;


import go.easy.gateway.client.TokenValidationClient;
import go.easy.gateway.client.ValidateTokenRequest;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Component
public class CustomAuthorizationFilterFactory extends AbstractGatewayFilterFactory<CustomAuthorizationFilterFactory.Config> {

    private final TokenValidationClient tokenValidationClient;


    public CustomAuthorizationFilterFactory(@Lazy TokenValidationClient tokenValidationClient) {
        super(Config.class);
        this.tokenValidationClient = tokenValidationClient;
    }

    @Override
    @SneakyThrows
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var token = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization")
                    .split(" ")[1];

            // Проверьте токен на валидность и выполните необходимую логику авторизации
            boolean isAuthorized = false;
            try {
                isAuthorized = performAuthorization(token);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }


            if (isAuthorized) {
                return chain.filter(exchange);
            } else {
                // Если запрос не авторизован, верните ошибку 401 Unauthorized
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };

    }

    private boolean performAuthorization(String token) throws ExecutionException, InterruptedException, TimeoutException {
        var request = ValidateTokenRequest.builder()
                .tokenValue(token)
                .build();
        return tokenValidationClient.validateToken(request);
    }

    public static class Config {
    }
}
