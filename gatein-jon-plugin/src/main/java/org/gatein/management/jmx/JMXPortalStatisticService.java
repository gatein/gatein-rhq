/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.gatein.management.jmx;

import org.gatein.common.util.ParameterValidation;
import org.gatein.management.PortalStatisticService;
import org.mc4j.ems.connection.bean.EmsBean;
import org.mc4j.ems.connection.bean.operation.EmsOperation;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class JMXPortalStatisticService implements PortalStatisticService
{
   private final EmsOperation getThroughput;
   private final EmsOperation getMinTime;
   private final EmsOperation getMaxTime;
   private final EmsOperation getAverageTime;
   private final String portalName;

   public JMXPortalStatisticService(EmsBean statisticJMXBean, String portalName)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(statisticJMXBean, "JMX Statistic proxy");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(portalName, "Portal name", null);

      getThroughput = statisticJMXBean.getOperation("getThroughput");
      getMinTime = statisticJMXBean.getOperation("getMinTime");
      getMaxTime = statisticJMXBean.getOperation("getMaxTime");
      getAverageTime = statisticJMXBean.getOperation("getAverageTime");

      this.portalName = portalName;
   }

   @Override
   public double getThroughput()
   {
      return (Double)getThroughput.invoke(portalName);
   }

   @Override
   public double getMinExecutionTime()
   {
      return (Double)getMinTime.invoke(portalName);
   }

   @Override
   public double getMaxExecutionTime()
   {
      return (Double)getMaxTime.invoke(portalName);
   }

   @Override
   public double getAverageExecutionTime()
   {
      return (Double)getAverageTime.invoke(portalName);
   }
}
