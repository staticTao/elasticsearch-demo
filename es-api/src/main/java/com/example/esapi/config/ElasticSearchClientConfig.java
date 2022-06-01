package com.example.esapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:创建elasticsearch待用
 * @Author: TGP
 * @Date: 2022/5/10 10:10
 **/
@Configuration
public class ElasticSearchClientConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http")
//                ,new HttpHost("127.0.0.1", 9200, "http") //集群的话 配置多个
                )
        );
        return client;
    }
}
