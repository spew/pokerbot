
package org.poker.irc.espn;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class Links {

    @Expose
    private Api api;
    @Expose
    private Web web;
    @Expose
    private Mobile mobile;

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Web getWeb() {
        return web;
    }

    public void setWeb(Web web) {
        this.web = web;
    }

    public Mobile getMobile() {
        return mobile;
    }

    public void setMobile(Mobile mobile) {
        this.mobile = mobile;
    }

}
