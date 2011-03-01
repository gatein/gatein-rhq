/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.gatein.management.jonplugin;

import org.gatein.management.Portal;
import org.gatein.management.PortalStatisticService;
import org.gatein.management.jmx.JMXPortalStatisticService;
import org.mc4j.ems.connection.EmsConnection;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.plugins.jmx.JMXComponent;
import org.rhq.plugins.jmx.MBeanResourceComponent;

import java.util.Set;


public class PortalComponent extends MBeanResourceComponent<JMXComponent> implements MeasurementFacet, OperationFacet
{
   private Portal portal;
   private AvailabilityType availability = AvailabilityType.UP;

   public AvailabilityType getAvailability()
   {
      return availability;
   }

   public void start(ResourceContext<JMXComponent> context)
   {
      String resourceKey = context.getResourceKey();
      try
      {
         EmsConnection connection = context.getParentResourceComponent().getEmsConnection();

         ResourceKey key = ResourceKey.parse(resourceKey);

         Portal.PortalKey portalKey = key.getPortalKey();
         String currentPortalName = portalKey.getPortalName();

         PropertySimple objectName = (PropertySimple)context.getPluginConfiguration().get("objectName");

         // configuration variables are not interpolated when retrieved from component for some reason (they are from discovery)
         // also, exo registered the bean with the container name in quotes so need to add them so that the bean can be found...
         String beanName = objectName.getStringValue().replace("%container%", "\"" + portalKey.getPortalContainerName() + "\"");
         EmsBean portalBean = connection.getBean(beanName);

         String[] portalNames = (String[])portalBean.getAttribute("PortalList").getValue();
         for (String portalName : portalNames)
         {
            if (portalName.equals(currentPortalName))
            {
               PortalStatisticService statisticService = new JMXPortalStatisticService(portalBean, portalName);
               portal = new Portal(Portal.PortalKey.create(portalKey.getPortalContainerName(), currentPortalName), statisticService);
               break;
            }
         }

         availability = AvailabilityType.UP;
      }
      catch (Exception e)
      {
         log.debug("Couldn't start PortalComponent '" + resourceKey + "'", e);
         availability = AvailabilityType.DOWN;
      }
   }

   public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics)
   {
      for (MeasurementScheduleRequest req : metrics)
      {
         String name = req.getName();
         if (name.equals("averageExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getAverageExecutionTime());
            report.addData(res);
         }
         else if (name.equals("minExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getMinExecutionTime());
            report.addData(res);
         }
         else if (name.equals("maxExecutionTime"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getMaxExecutionTime());
            report.addData(res);
         }
         else if (name.equals("throughput"))
         {
            MeasurementDataNumeric res = new MeasurementDataNumeric(req, portal.getThroughput());
            report.addData(res);
         }

      }
   }

   public OperationResult invokeOperation(String name, Configuration params) throws Exception
   {
      OperationResult res = new OperationResult();
      if ("refreshPortletList".equals(name))
      {
         System.out.println("refreshPortletList called res = " + res);
      }
      return res;

   }
}