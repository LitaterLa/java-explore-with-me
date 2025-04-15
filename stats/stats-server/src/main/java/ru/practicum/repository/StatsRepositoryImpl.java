package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final NamedParameterJdbcTemplate jdbc;


    @Override
    public EndpointHit saveHit(EndpointHit hit) {
        String query = "INSERT INTO stats (app, uri, ip, created) " +
                "VALUES (:app, :uri, :ip, :created) " +
                "RETURNING id, app, uri, ip, created";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("app", hit.getApp())
                .addValue("uri", hit.getUri())
                .addValue("ip", hit.getIp())
                .addValue("created", hit.getCreated());

        return jdbc.queryForObject(query, params, (rs, rowNum) ->
                EndpointHit.builder()
                        .id(rs.getInt("id"))
                        .app(rs.getString("app"))
                        .uri(rs.getString("uri"))
                        .ip(rs.getString("ip"))
                        .created(rs.getObject("created", LocalDateTime.class))
                        .build());

    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String hitCount = unique ? "COUNT(DISTINCT s.ip)" : "COUNT(s.ip)";

        String query = "SELECT s.app, s.uri, " + hitCount + " AS hits " +
                "FROM stats s " +
                "WHERE s.created BETWEEN :start AND :end ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start)
                .addValue("end", end);

        if (uris != null && !uris.isEmpty()) {
            query += " AND s.uri IN (:uris)";
            params.addValue("uris", uris);

        }

        query += "GROUP BY s.app, s.uri ORDER BY hits DESC";

        return jdbc.query(query, params, (rs, rowNum) ->
                ViewStats.builder()
                        .app(rs.getString("app"))
                        .uri(rs.getString("uri"))
                        .hits(rs.getInt("hits"))
                        .build());

    }


}
