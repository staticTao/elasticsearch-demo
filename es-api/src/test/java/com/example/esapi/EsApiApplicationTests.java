package com.example.esapi;

import com.alibaba.fastjson.JSON;
import com.example.esapi.entity.User;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 使用es 7.6.x 高级客户端测试api
 * @Author: TGP
 * @Version: v1.0
 * @Date: 2022/5/10 10:39
 **/
@SpringBootTest
class EsApiApplicationTests {

    @Autowired
//    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    /**
     * @Description: 创建索引
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 10:49
     * @Param
     * @Return * @return: void
     **/
    @Test
    void createIndex() throws IOException {
        //1.创建索引
        CreateIndexRequest request = new CreateIndexRequest("jd_goods");
        //客户端执行请求 ，获取响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    /**
     * @Description: 获取索引
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 10:55
     * @Param
     * @Return * @return: void
     **/
    @Test
    void existIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("tgp_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * @Description: 删除索引
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 11:01
     * @Param
     * @Return * @return: void
     **/
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test");
        AcknowledgedResponse acknowledged = client.indices().delete(request, RequestOptions.DEFAULT);
        //得到 boolean
        System.out.println(acknowledged.isAcknowledged());
    }

    /**
     * @Description: 添加文档数据
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 11:40
     * @Param
     * @Return * @return: void
     **/
    @Test
    void addDocument() throws IOException {
        User user = new User("TGP", 3);
        //创建连接
        IndexRequest request = new IndexRequest("tgp_index");
        //设置规则 put tgp_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端请求添加
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    /**
     * @Description: 获取文档
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 14:14
     * @Param
     * @Return * @return: void
     **/
    @Test
    void getDocument() throws IOException {
        GetRequest request = new GetRequest("tgp_index", "100");
        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString());
        System.out.println(documentFields);
    }

    /**
     * @Description: 更新文档
     * @Author: TGP
     * @Version: v1.0
     * @Date: 2022/5/10 15:46
     * @Param
     * @Return * @return: void
     **/
    @Test
    void updateDocument() throws IOException {
        User user = new User("tgp2", 5);
        UpdateRequest request = new UpdateRequest("tgp_index", "1");
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        request.timeout(TimeValue.timeValueSeconds(1));
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.getResult());
    }

    /**
     @Description: 删除文档信息
     @Author: TGP
     @Version: v1.0
     @Date: 2022/5/11 14:13 
     @Param  
     @Return * @return: void
    **/
    @Test
    void deleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("tgp_index", "1");
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.getResult());
    }
    /**
     @Description: 批量添加数据
     @Author: TGP
     @Version: v1.0
     @Date: 2022/5/11 14:18 
     @Param  
     @Return * @return: void
    **/
    @Test
    void bulkDocument() throws IOException {
        List<User> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            User user = new User();
            user.setName("tgp"+i);
            Random rd = new Random();
            user.setAge(rd.nextInt(20)+10);
            list.add(user);
        }

        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        for (int i = 0; i < list.size(); i++) {
            request.add(
                    new IndexRequest("tgp_index")
                            .id(""+(i+1))
                            .source(JSON.toJSONString(list.get(i)),XContentType.JSON)
            );
        }

        BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    @Test
    void searchDocument() throws IOException {
        SearchRequest request = new SearchRequest("tgp_index");
        //创建条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        /**
         * termQuery:精确匹配
         * matchAllQuery:全匹配所有
         * */
        //QueryBuilders 查询构造器添加条件
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", "tgp2");
        builder.query(queryBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(builder);
        //创建RestHighLevelClient 客户端连接
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(search.getHits()));
    }

    public static void main(String[] args) {
        String str = "{'name':'sss'}";
        HashMap hashMap = JSON.parseObject(str, HashMap.class);
        System.out.println(hashMap);
        System.out.println(hashMap.get("name"));
    }
}
