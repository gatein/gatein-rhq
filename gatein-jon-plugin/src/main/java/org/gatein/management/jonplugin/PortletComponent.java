/*
 * JBoss, a division of Red Hat
 * Copyright 2011, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
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

package org.gatein.management.jonplugin;

import org.gatein.management.Portlet;
import org.gatein.management.ResourceKey;
import org.gatein.management.jmx.GateInJMXResourceComponent;
import org.gatein.management.jmx.JMXPortletStatisticService;
import org.gatein.management.spi.stats.PortletStatisticService;
import org.mc4j.ems.connection.bean.EmsBean;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class PortletComponent extends GateInJMXResourceComponent<Portlet, PortletStatisticService>
{
   @Override
   protected MeasurementDataNumeric getMeasurementOrNullFor(MeasurementScheduleRequest req, String name)
   {
      if (name.equals("executionCount"))
      {
         return new MeasurementDataNumeric(req, 0.0d + managedResource.getExecutionCount());
      }
      else
      {
         return null;
      }
   }

   @Override
   protected void initManagedResource(ResourceKey key, EmsBean statisticBean)
   {
      PortletStatisticService statisticService = new JMXPortletStatisticService(statisticBean, getCurrentResourceName(key));
      managedResource = new Portlet(key, statisticService);
   }

   @Override
   protected String getCurrentResourceName(ResourceKey key)
   {
      return key.getPortletId();
   }

   @Override
   public String getAttributeName()
   {
      return "ApplicationList";
   }
}
