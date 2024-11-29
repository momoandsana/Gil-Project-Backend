package com.web.gilproject.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 엘라스틱서치 인덱싱
     * */
    public String indexDocument(String indexName, String id, Map<String, Object> document) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(id)
                .document(document)
            );

            return response.result().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "검색어 인덱싱 실패";
        }
    }

    /**
     * 엘라스틱서치 단일필드 검색기능
     * */
    public List<String> search(String indexName, String field, String value) {
        try {
            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                            .index(indexName)
                            .query(q -> q
                                    .match(t -> t
                                            .field(field)
                                            .query(value)
                                            .fuzziness("AUTO") // 유사도 설정
                                    )
                            ),
                    Map.class
            );

            return response.hits().hits().stream()
                    .map(Hit::id)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 엘라스틱서치 복수필드 검색기능
     * */
    public List<String> multiFieldSearch(String indexName, String searchText, List<String> fields) {
        try {
            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                            .index(indexName)
                            .query(q -> q
                                    .multiMatch(mm -> mm
                                            .query(searchText)   // 검색어
                                            .fields(fields)     // 검색할 필드 목록
                                            .fuzziness("1") // 유사도 설정
                                    )
                            ),
                    Map.class
            );

            // 문서 ID 목록 반환
            return response.hits().hits().stream()
                    .map(Hit::id)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * 엘라스틱서치 인덱싱 삭제
     * */
    public String deleteDocument(String indexName, String id) {
        try {
            DeleteResponse response = elasticsearchClient.delete(d -> d
                    .index(indexName)
                    .id(id)
            );

            return response.result().toString(); // 결과 반환 (DELETED, NOT_FOUND 등)
        } catch (Exception e) {
            e.printStackTrace();
            return "인덱싱 삭제 실패";
        }
    }
}