/*
 *    Copyright 2010 The MyBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.caches.memcached;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;

/**
 * Setter from String to ConnectionFactory representation.
 *
 * @version $Id$
 */
final class ConnectionFactorySetter extends AbstractPropertySetter<ConnectionFactory> {

    /**
     * Instantiates a String to ConnectionFactory setter.
     */
    public ConnectionFactorySetter() {
        super("com.google.code.ibaguice.memcached.connectionfactory",
                "connectionFactory",
                new DefaultConnectionFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConnectionFactory convert(String property) throws Throwable {
        Class<?> clazz = Class.forName(property);
        if (!ConnectionFactory.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class '"
                    + clazz.getName()
                    + "' is not a valid '"
                    + ConnectionFactory.class.getName()
                    + "' implementation");
        }
        return (ConnectionFactory) clazz.newInstance();
    }

}
