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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * This class contains helper methods for working with java beans
 *
 * @author Santhosh Kumar T
 */
public class BeanUtil{
    public static String getMethodSuffix(String property){
        switch(property.length()){
            case 0:
                throw new IllegalArgumentException("invalid property name: "+property);
            case 1:
                return property.toUpperCase(Locale.ENGLISH);
            default:
                char char0 = property.charAt(0);
                boolean upper0 = Character.isUpperCase(char0);
                char char1 = property.charAt(1);
                boolean upper1 = Character.isUpperCase(char1);
                if(upper0 && upper1) // XCoordinate ==> getXCordinate()
                    return property;
                if(!upper0 && !upper1) // xcoordinate ==> getXcoordinate()
                    return Character.toUpperCase(char0)+property.substring(1);
                if(!upper0 && upper1) // xCoordinate ==> getxCoordinate()
                    return property;
                throw new IllegalArgumentException("invalid property name: "+property);
        }
    }

    @SuppressWarnings({"ConstantConditions"})
    public static String getPropertyName(String methodName){
        String suffix;
        if(methodName.startsWith(GET) || methodName.startsWith(SET))
            suffix = methodName.substring(3);
        else if(methodName.startsWith(IS))
            suffix = methodName.substring(2);
        else
            throw new IllegalArgumentException("invalid method name: "+methodName);

        switch(suffix.length()){
            case 0:
                throw new IllegalArgumentException("invalid method name: "+methodName);
            case 1:
                return suffix.toLowerCase(Locale.ENGLISH);
            default:
                char char0 = suffix.charAt(0);
                boolean upper0 = Character.isUpperCase(char0);
                char char1 = suffix.charAt(1);
                boolean upper1 = Character.isUpperCase(char1);
                if(upper0 && upper1) // getXCordinate() ==> XCoordinate
                    return suffix;
                if(upper0 && !upper1) // getXcoordinate() ==> xcoordinate
                    return Character.toLowerCase(char0)+suffix.substring(1);
                if(!upper0 && upper1) // getxCoordinate() ==> xCoordinate
                    return suffix;
                throw new IllegalArgumentException("invalid method name: "+methodName);
        }
    }

    /*-------------------------------------------------[ Getter Method ]---------------------------------------------------*/

    /** prefix used by non-boolean getter methods */
    public static final String GET = "get"; //NOI18N
    /** prefix used by boolean getter methods */
    public static final String IS  = "is";  //NOI18N

    /**
     * Returns getter method for <code>property</code> in specified <code>beanClass</code>
     *
     * @param beanClass bean class
     * @param property  name of the property
     *
     * @return getter method. null if <code>property</code> is not found
     *
     * @see #getGetterMethod(Class, String, Class)
     */
    public static Method getGetterMethod(Class<?> beanClass, String property) throws NoSuchMethodException{
        try{
            return beanClass.getMethod(GET+getMethodSuffix(property));
        }catch(NoSuchMethodException ex1){
            return beanClass.getMethod(IS+getMethodSuffix(property));
        }
    }

    /**
     * Returns getter method for <code>property</code> in specified <code>beanClass</code>
     *
     * @param beanClass bean class
     * @param property  name of the property
     * @param propertyType type of the property. This is used to compute getter method name.
     *
     * @return getter method. null if <code>property</code> is not found
     *
     * @see #getGetterMethod(Class, String)
     */
    public static Method getGetterMethod(Class<?> beanClass, String property, Class propertyType) throws NoSuchMethodException{
        try{
            if(propertyType==boolean.class)
                return beanClass.getMethod(IS+getMethodSuffix(property));
        }catch(NoSuchMethodException ignore){
            // ignore
        }
        return beanClass.getMethod(GET+getMethodSuffix(property));
    }

    /*-------------------------------------------------[ Setter Method ]---------------------------------------------------*/

    /**
     * Returns setter method for <code>property</code> in specified <code>beanClass</code>
     *
     * @param beanClass bean class
     * @param property  name of the property
     *
     * @return setter method. null if <code>property</code> is not found, or it is readonly property
     *
     * @see #getSetterMethod(Class, String, Class)
     */
    public static Method getSetterMethod(Class<?> beanClass, String property) throws NoSuchMethodException{
        Method getter = getGetterMethod(beanClass, property);
        return getSetterMethod(beanClass, property, getter.getReturnType());
    }

    /** prefix used by setter methods */
    public static final String SET = "set"; //NOI18N

    /**
     * Returns setter method for <code>property</code> in specified <code>beanClass</code>
     *
     * @param beanClass bean class
     * @param property  name of the property
     * @param propertyType type of the property. This is used to compute setter method name.
     *
     * @return setter method. null if <code>property</code> is not found, or it is readonly property
     *
     * @see #getSetterMethod(Class, String)
     */
    public static Method getSetterMethod(Class<?> beanClass, String property, Class propertyType) throws NoSuchMethodException{
        return beanClass.getMethod(SET+getMethodSuffix(property), propertyType);
    }

    /*-------------------------------------------------[ Property ]---------------------------------------------------*/

    /**
     * Returns the type of <code>property</code> in given <code>beanClass</code>
     *
     * @param beanClass bean class
     * @param property  name of the property
     *
     * @return  null if the property is not found. otherwise returns the type of the <code>property</code>
     */
    public static Class getPropertyType(Class<?> beanClass, String property) throws NoSuchMethodException{
        Method getter = getGetterMethod(beanClass, property);
        return getter.getReturnType();
    }

    /**
     * Returns the value of the specified <code>property</code> in given <code>bean</code>
     *
     * @param bean      bean object
     * @param property  property name whose value needs to be returned
     *
     * @return  value of the property.
     *
     * @throws InvocationTargetException if method invocation fails
     * @throws NullPointerException if <code>property</code> is not found in <code>bean</code>
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getProperty(Object bean, String property) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
        return (T)getGetterMethod(bean.getClass(), property).invoke(bean);
    }

    /**
     * Sets the value of the specified <code>property</code> in given <code>bean</code>
     *
     * @param bean      bean object
     * @param property  property name whose value needs to be set
     * @param value     value to be set
     *
     * @throws InvocationTargetException if method invocation fails
     * @throws NullPointerException if <code>property</code> is not found in <code>bean</code> or it is readonly property
     */
    public static void setProperty(Object bean, String property, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException{
        getSetterMethod(bean.getClass(), property).invoke(bean, value);
    }
}