package com.profITsoft.emailservice.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

/**
 * Data class for email entity to be stored in Elasticsearch.
 */
@Getter
@Setter
@Document(indexName = "emails")
public class EmailData {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String recipient;

    @Field(type = FieldType.Text)
    private String subject;

    @Field(type = FieldType.Text)
    private String text;

    @Field(type = FieldType.Keyword)
    private StatusData status;

    @Field(type = FieldType.Text)
    private String errorMessage;

    @Field(type = FieldType.Integer)
    private int attemptCount;

    @Field(type = FieldType.Date)
    private Instant lastAttemptTime;

}
