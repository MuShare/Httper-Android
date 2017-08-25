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
        @Index(value = "createAt ASC", unique = true)
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

    @NotNull
    private String headers;

    @NotNull
    private String parameters;

    @NotNull
    private String body;

    public RequestRecord(Long id) {
        this.id = id;
    }


    @Generated(hash = 2099839459)
    public RequestRecord(Long id, @NotNull Long createAt, @NotNull String method, @NotNull String
            http,
                         @NotNull String url, @NotNull String headers, @NotNull String parameters,
                         @NotNull String body) {
        this.id = id;
        this.createAt = createAt;
        this.method = method;
        this.http = http;
        this.url = url;
        this.headers = headers;
        this.parameters = parameters;
        this.body = body;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RequestRecord && getMethod().equals(((RequestRecord) obj).getMethod
                ()) && getUrl().equals(((RequestRecord) obj).getUrl()) && getHttp().equals((
                (RequestRecord) obj).getHttp()) && getHeaders().equals(((RequestRecord)
                obj).getHeaders()) && getParameters().equals(((RequestRecord) obj).getParameters
                ()) && getBody().equals(((RequestRecord) obj).getBody());
    }

    @Override
    public int hashCode() {
        return getMethod().hashCode() + getUrl().hashCode() + getHttp().hashCode() + getHeaders()
                .hashCode() + getParameters().hashCode() + getBody().hashCode();
    }
}