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
package org.raml.parser.utils;

import java.lang.reflect.Constructor;

import org.apache.commons.lang.ClassUtils;

public class ConvertUtils
{

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T convertTo(String value, Class<T> type)
    {
        if (type.isEnum())
        {
            return type.cast(Enum.valueOf((Class) type, value.toUpperCase()));
        }
        Class<T> clazz = type;
        if (type.isPrimitive())
        {
            clazz = ClassUtils.primitiveToWrapper(type);
        }
        if (clazz.getName().equals(Boolean.class.getName()))
        {
            try
            {
                String stringValue = value.toString();
                if (stringValue.equalsIgnoreCase("yes") || stringValue.equals("y")
                        || stringValue.equalsIgnoreCase("true") || stringValue.equals("t"))
                {
                    return (T)(Boolean.TRUE);
                }
                else if (stringValue.equalsIgnoreCase("no") || stringValue.equals("n")
                        || stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("f"))
                {
                    return (T)(Boolean.FALSE);
                }
                else
                {
                    throw new ConversionException(stringValue);
                }
            }
            catch (ClassCastException e)
            {
                throw new ConversionException(e);
            }
        }
        try
        {
            Constructor constructor = type.getConstructor(String.class);
            return (T) constructor.newInstance(value);
        }
        catch (Exception e)
        {
            throw new ConversionException(e);
        }
    }

    public static boolean canBeConverted(String value, Class<?> type)
    {
        if (type.isEnum())
        {
            Object[] enumConstants = type.getEnumConstants();
            for (Object enumConstant : enumConstants)
            {
                if (enumConstant.toString().equals(value.toUpperCase()))
                {
                    return true;
                }
            }
            return false;
        }
        if (type.isInstance(value))
        {
            return true;
        }
        try
        {
            type.getConstructor(String.class);
            return true;
        }
        catch (NoSuchMethodException e)
        {
            //ignore
        }
        try
        {
            Class<?> wrapperClass = ClassUtils.primitiveToWrapper(type);
            convertTo(value, wrapperClass);
            return true;
        }
        catch (ClassCastException e)
        {
            return false;
        }
        catch (ConversionException e)
        {
            return false;
        }
    }
}
