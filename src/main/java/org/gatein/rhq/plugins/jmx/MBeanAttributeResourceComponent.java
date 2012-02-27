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
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.bean.EmsBean;
import org.mc4j.ems.connection.bean.operation.EmsOperation;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.DataType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceComponent;

import java.util.Set;

/**
 * This class will lookup measurement values by invoking the metric as an operation on the JMX bean using the
 * attribute obtained from {@link MBeanAttributeDiscoveryComponent} as the parameter to that operation.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public abstract class MBeanAttributeResourceComponent implements MeasurementFacet, JMXComponent<MBeanResourceComponent<?>>
{
   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins.jmx");
   
   private MBeanResourceComponent<?> parent;
   private EmsBean bean;
   private String attributeValue;

   // ---------------------------------------- MeasurementFacet Impl ----------------------------------------
   @Override
   public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception
   {
      boolean trace = log.isTraceEnabled();
      for (MeasurementScheduleRequest metric : metrics)
      {
         if (metric.getDataType() == DataType.MEASUREMENT)
         {
            EmsOperation operation = getOperation(bean, metric.getName());
            if (operation != null)
            {
               if (trace) log.trace("Gathering metric data by invoking operation " + operation.getName() + " with parameter " + attributeValue + " on JMX bean " + bean.getBeanName());
               Object value = operation.invoke(attributeValue);
               if (value instanceof Number)
               {
                  Double doubleValue = ((Number) value).doubleValue();
                  if (trace) log.trace("Found numeric value " + doubleValue + " for metric data for " + metric.getName());
                  report.addData(new MeasurementDataNumeric(metric, doubleValue));
               }
               else 
               {
                  log.warn("Value returned for operation " + operation.getName() + " and JMX bean " + bean.getBeanName() + " was not a numeric value.");
               }
            }
         }
         else
         {
            log.warn("Data type " + metric.getDataType() + " not supported.");
         }
      }
   }

   // ---------------------------------------- ResourceComponent Impl ----------------------------------------
   @Override
   public void start(ResourceContext<MBeanResourceComponent<?>> context) throws InvalidPluginConfigurationException, Exception
   {
      parent = context.getParentResourceComponent();
      bean = parent.getEmsBean();
      attributeValue = parseAttributeValue(context.getResourceKey());
      // An interesting way to override the parent's objectName
      String objectName = context.getPluginConfiguration().getSimpleValue(MBeanResourceComponent.OBJECT_NAME_PROP, null);
      if (objectName != null)
      {
         // Resolve any template variables or w/e you want to call them
         objectName = TemplateResolver.resolve(objectName, parent.getResourceContext().getPluginConfiguration());
         bean = getEmsConnection().getBean(objectName);
      }
   }

   @Override
   public void stop()
   {
      parent = null;
      attributeValue = null;
      bean = null;
   }

   @Override
   public AvailabilityType getAvailability()
   {
      return parent.getAvailability();
   }

   // ---------------------------------------- JMXComponent Impl ----------------------------------------
   @Override
   public EmsConnection getEmsConnection()
   {
      return parent.getEmsConnection();
   }

   /**
    * This method allows the implementing class to resolve/parse the attributeValue back from the resourceKey which was
    * created during the {@link MBeanAttributeDiscoveryComponent#createResourceDetails(org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext, String)}
    * method.
    *
    * @param resourceKey The resourceKey of the discovered resource.
    * @return the attribute value
    */
   protected abstract String parseAttributeValue(String resourceKey);
   
   
   private EmsOperation getOperation(EmsBean bean, String operationName)
   {
      EmsOperation operation = bean.getOperation(operationName, String.class);
      if (operation != null) return operation;

      // construct getter method, ie executionCount becomes getExecutionCount
      StringBuilder sb = new StringBuilder(operationName.substring(0,1)
         .toUpperCase()).insert(0, "get").append(operationName.substring(1, operationName.length()));
      String getOperationName = sb.toString();

      operation = bean.getOperation(getOperationName, String.class);
      if (operation != null) return operation;
      
      log.warn("Operation '" + operationName + " or '" + getOperationName + "' not found on JMX bean " + bean.getBeanName());
      return null;
   }
}
