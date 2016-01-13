/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.model;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Operation implements Serializable {

    @Scalar(required = true)
    private String name;

    @Scalar
    private String payloadParameter;

    @Mapping
    private Map<String, String> parameterAliases = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayloadParameter() {
        return payloadParameter;
    }

    public void setPayloadParameter(String payloadParameter) {
        this.payloadParameter = payloadParameter;
    }

    public Map<String, String> getParameterAliases() {
        return parameterAliases;
    }

    public void setParameterAliases(Map<String, String> parameterAliases) {
        this.parameterAliases = parameterAliases;
    }
}
