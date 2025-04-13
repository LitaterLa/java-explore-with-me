package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class StatsClient {
    private final RestClient client;
    private static final String HIT_PATH = "/hit";
    private static final String STATS_PATH = "/stats";


    public void sendHit(EndpointHit hit) {
        client.post()
                .uri(HIT_PATH)
                .body(hit)
                .retrieve()
                .onStatus(code -> code == HttpStatus.BAD_REQUEST,
                        (request, response) -> {
                            String error = new String(response.getBody().readAllBytes());
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка в данных: " + error);
                        })
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR, "Сервис статистики временно недоступен"
                            );
                        }
                )
                .toBodilessEntity();
    }


    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(STATS_PATH)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .onStatus(code -> code == HttpStatus.BAD_REQUEST,
                        (request, response) -> {
                            String error = new String(response.getBody().readAllBytes());
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка в данных: " + error);
                        })
                .body(new ParameterizedTypeReference<>() {
                });
    }
}

