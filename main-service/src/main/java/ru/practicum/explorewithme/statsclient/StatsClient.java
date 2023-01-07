package ru.practicum.explorewithme.statsclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explorewithme.statsclient.endpoint.EndpointHit;
import ru.practicum.explorewithme.statsclient.endpoint.Views;
import ru.practicum.explorewithme.statsclient.endpoint.ViewsStats;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Component
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stats-service.url}")
    private String serverUrl;

    public List<ViewsStats> getViews(String start, String end, List<String> uris, Boolean unique) {
        String uri = serverUrl + "/stats?start=" + start + "&end=" + end;
        if (uris == null) {
            uri = uri + "&unique=" + unique;
        } else {
            uri = uri + "&uris=";
        }
        try {
            Views response = restTemplate.getForObject(new URI(uri), Views.class);
                return response != null ? response.getViewsStats() : null;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<ViewsStats> postEndpointHit(EndpointHit hit) {
        String uri = serverUrl + "/hit";
        try {
            return restTemplate.postForEntity(new URI(uri), hit, ViewsStats.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
