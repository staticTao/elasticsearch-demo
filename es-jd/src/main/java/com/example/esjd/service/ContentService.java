package com.example.esjd.service;

/**
 * @Description:TODO
 * @Author: TGP
 * @Date: 2022/5/30 9:36
 **/

import com.alibaba.fastjson.JSON;
import com.example.esjd.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient client;

    public Boolean parseContent(String keyword) throws IOException {
        //获取所有数据
        List<Map> contents = HtmlParseUtil.getParseHtml(keyword);
        //批量填充到es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(contents.get(i), XContentType.JSON));
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
    /**
     @Description: 分页搜索
     @Author: TGP
     @Version: v1.0
     @Date: 2022/6/1 10:26
     @Param * @param keyword: 关键字
            * @param pageNo: : 开始页数
            * @param pageSize:  每页条数
     @Return * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    public List<Map<String,Object>> search(String keyword,int pageNo,int pageSize) throws IOException {
        //获取所有数据
        List<Map> contents = HtmlParseUtil.getParseHtml(keyword);
        //批量填充到es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(contents.get(i), XContentType.JSON));
        }
        //
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (pageNo<1){
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        MatchQueryBuilder builder = QueryBuilders.matchQuery("name", keyword);
        sourceBuilder.query(builder);
        //执行搜索
        sourceBuilder.timeout(new TimeValue(2, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }
    /**
     @Description: 搜索 -- 高亮显示
     @Author: TGP
     @Version: v1.0
     @Date: 2022/5/30 15:29
     @Param * @param keyword: 关键字输入
            * @param pageNo: 开始页数
            * @param pageSize:  一页显示数量
     @Return * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    public List<Map<String,Object>> highLightSearch(String keyword,int pageNo,int pageSize) throws IOException {
        //获取所有数据
        List<Map> contents = HtmlParseUtil.getParseHtml(keyword);
        //批量填充到es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(contents.get(i), XContentType.JSON));
        }
        //
        client.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (pageNo<1){
            pageNo = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        //精准匹配
        MatchQueryBuilder builder = QueryBuilders.matchQuery("name", keyword);
        sourceBuilder.query(builder);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        sourceBuilder.timeout(new TimeValue(2, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {
            //解析高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//原来的结果
            //将原来的字段换成高亮的字段
            if (name!=null){
                Text[] fragments = name.fragments();
                String newName = "";
                for (Text text : fragments) {
                    newName += text;
                }
                //替换name的值
                sourceAsMap.put("name",newName);
            }
            //添加到list
            list.add(sourceAsMap);
        }
        return list;
    }

}
