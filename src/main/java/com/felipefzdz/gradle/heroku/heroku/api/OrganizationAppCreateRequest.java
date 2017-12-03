package com.felipefzdz.gradle.heroku.heroku.api;

import com.heroku.api.Heroku;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrganizationAppCreateRequest extends BaseHerokuApiJsonRequest<Map<String, ?>> {

    private final String appName;
    private final String organizationName;
    private final Heroku.Stack stack;
    private final boolean personal;

    public OrganizationAppCreateRequest(String appName, String organizationName, Heroku.Stack stack, boolean personal) {
        this.appName = appName;
        this.organizationName = organizationName;
        this.stack = stack;
        this.personal = personal;
    }

    @Override
    public String getEndpoint() {
        return "/organizations/apps";
    }

    @Override
    public Map<String, Object> getBodyAsMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(4);
        map.put("name", appName);
        map.put("organization", organizationName);
        map.put("personal", personal);
        map.put("stack", stack.value);
        return map;
    }

}
