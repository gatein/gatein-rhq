/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.rhq.plugins.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mc4j.ems.connection.bean.EmsBean;
import org.mc4j.ems.connection.bean.attribute.EmsAttribute;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.jmx.MBeanResourceComponent;

import java.util.HashSet;
import java.util.Set;

/**
 * This class will discover resource based on an 'listAttributeName' attribute of a JMX bean which returns a list
 * of values (String[]) to create resources from.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public abstract class MBeanAttributeDiscoveryComponent implements ResourceDiscoveryComponent<MBeanResourceComponent<?>>
{
   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins.jmx");

   @Override
   public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<MBeanResourceComponent<?>> context) throws InvalidPluginConfigurationException, Exception
   {
      boolean trace = log.isTraceEnabled();

      // Make sure listAttributeName is present in configuration.
      Configuration config = context.getDefaultPluginConfiguration();
      String listAttributeName = config.getSimpleValue("listAttributeName", null);
      if (listAttributeName == null) throw new InvalidPluginConfigurationException("listAttributeName is a required configuration property for this plugin.");

      // Get the JMX bean from the parent, or if an objectName is configured, use that instead.
      EmsBean bean = context.getParentResourceComponent().getEmsBean();
      String objectName = config.getSimpleValue(MBeanResourceComponent.OBJECT_NAME_PROP, null);
      if (objectName != null)
      {
         objectName = TemplateResolver.resolve(objectName, context.getParentResourceContext().getPluginConfiguration());
         bean = context.getParentResourceComponent().getEmsConnection().getBean(objectName);
      }
      if (bean == null) throw new Exception("JMX bean not found.");
      if (trace) log.trace("Using JMX bean " + bean.getBeanName() + " for this discovery component");

      // Get attribute that returns a list of values ot be discovered.
      EmsAttribute attribute = bean.getAttribute(listAttributeName);
      if (attribute == null) throw new Exception("Unknown attribute '" + listAttributeName + "' for JMX bean " + bean.getBeanName());
      if (trace) log.trace("Looking up list of values for attribute " + attribute.getName());

      // Discover
      Object object = attribute.getValue();
      if (object instanceof String[])
      {
         String[] values = (String[]) object;
         if (trace)
         {
            log.trace("Found " + values.length + " values for attribute " + attribute.getName());
         }
         Set<DiscoveredResourceDetails> details = new HashSet<DiscoveredResourceDetails>(values.length);
         for (String value : values)
         {
            if (trace)
            {
               log.trace("Creating resource detail for attribute value " + value);
            }
            details.add(createResourceDetails(context, value));
         }
         
         return details;
      }
      else
      {
         throw new Exception("Attribute '" + listAttributeName +"' for JMX bean " + bean.getBeanName() + " must return a string array (String[])");
      }
   }

   /**
    * Create DiscoveredResourceDetails based on values found during invocation of the 'listAttributeName' attribute of the
    * JMX bean.
    *
    * @param context the {@link ResourceDiscoveryContext} of the discovery request
    * @param attributeValue a value
    * @return
    */
   protected abstract DiscoveredResourceDetails createResourceDetails(ResourceDiscoveryContext<MBeanResourceComponent<?>> context, String attributeValue);
}
