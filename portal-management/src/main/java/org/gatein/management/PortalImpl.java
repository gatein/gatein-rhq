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

package org.gatein.management;


import org.gatein.common.util.ParameterValidation;

import java.util.Collections;
import java.util.Map;

/**
 * @author Chris Laprun
 * @version $Revision: 8805 $
 */
public class PortalImpl implements Portal
{
   private PortalKey key;
   private PortalStatisticService statisticService;

   public PortalImpl(PortalKey key, PortalStatisticService statisticService)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(key, "PortalKey");
      ParameterValidation.throwIllegalArgExceptionIfNull(statisticService, "PortalStatisticService");

      this.key = key;
      this.statisticService = statisticService;
   }

   public PortalKey getKey()
   {
      return key;
   }

   private String getPortalName()
   {
      return key.getPortalName();
   }

   public double getThroughput()
   {
      return statisticService.getThroughput();
   }

   public double getMinExecutionTime()
   {
      return statisticService.getMinExecutionTime();
   }

   public double getMaxExecutionTime()
   {
      return statisticService.getMaxExecutionTime();
   }

   public double getAverageExecutionTime()
   {
      return statisticService.getAverageExecutionTime();
   }

   public Map<String, ManagedPortletInvoker> getManagedPortletInvokers()
   {
//      return PortalServer.getManagedPortletInvokersFor(invoker);
      return Collections.emptyMap();
   }

   public int compareTo(Portal o)
   {
      return key.compareTo(o.getKey());
   }
}
