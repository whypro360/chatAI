package com.main.chatai.entity.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

@Data
public class ElderlyCourseQuery {
    @ToolParam(required = false, description = "课程类型:文化艺术、体育健身、保健养生、现代科技、心理健康、兴趣拓展等")
    private String type;
    @ToolParam(required = false, description = "适合年龄:建议填写老人的实际年龄")
    private Integer age;
    @ToolParam(required = false, description = "排序方式")
    private List<Sort> sorts;

    @Data
    public static class Sort {
        @ToolParam(required = false, description = "排序字段:duration（学习时长）等")
        private String field;
        @ToolParam(required = false, description = "是否是升序:true/false")
        private Boolean asc;
    }
}