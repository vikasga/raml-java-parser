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
package org.raml.parser.tagresolver;

import static org.raml.parser.tagresolver.JacksonTagResolver.JACKSON_TAG;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * This tag resolver validates that the classes referenced by
 * jackson and jaxb tagged nodes are present.
 */
public class PojoValidatorTagResolver implements TagResolver
{

    @Override
    public boolean handles(Tag tag)
    {
        return JACKSON_TAG.equals(tag);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        String className = ((ScalarNode) node).getValue();
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        //error thrown when class name differ in case
        catch (NoClassDefFoundError e)
        {
            nodeHandler.onCustomTagError(node.getTag(), node, "Class not found " + className);
        }
        catch (ClassNotFoundException e)
        {
            nodeHandler.onCustomTagError(node.getTag(), node, "Class not found " + className);
        }
        return node;
    }

    @Override
    public void beforeProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
    {
        //do nothing
    }

    @Override
    public void afterProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
    {
        //do nothing
    }

}
