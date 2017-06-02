package org.mushare.httper.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by dklap on 5/23/2017.
 * Entity mapped to table "RequestRecord".
 */
@Entity(indexes = {
        @Index(value = "createAt DESC", unique = true)
})
public class RequestRecord {

    @Id
    private Long id;

    @NotNull
    private Long createAt;

    @NotNull
    private String method;

    @NotNull
    private String http;

    @NotNull
    private String url;

    private String headers;
    private String parameters;


    public RequestRecord(Long id) {
        this.id = id;
    }


    @Generated(hash = 578849419)
    public RequestRecord(Long id, @NotNull Long createAt, @NotNull String method,
                         @NotNull String http, @NotNull String url, String headers,
                         String parameters) {
        this.id = id;
        this.createAt = createAt;
        this.method = method;
        this.http = http;
        this.url = url;
        this.headers = headers;
        this.parameters = parameters;
    }


    @Generated(hash = 1183636705)
    public RequestRecord() {
    }


    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Long getRid() {
        return this.id;
    }

    public void setRid(Long id) {
        this.id = id;
    }

    public String getHttp() {
        return this.http;
    }

    public void setHttp(String http) {
        this.http = http;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}