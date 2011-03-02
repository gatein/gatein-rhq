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

package org.gatein.management;

import org.gatein.common.util.ParameterValidation;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public abstract class ManagedResource<T extends ManagedResource, S extends TimedStatisticService> implements Comparable<T>
{
   protected final ResourceKey key;
   protected final S statisticService;

   public ManagedResource(ResourceKey key, S statisticService)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(key, "key");
      ParameterValidation.throwIllegalArgExceptionIfNull(statisticService, "statistic service");

      this.key = key;
      this.statisticService = statisticService;
   }

   public ResourceKey getKey()
   {
      return key;
   }

   public S getStatisticService()
   {
      return statisticService;
   }

   public String getResourceName()
   {
      return statisticService.getServiceName();
   }

   public int compareTo(T o)
   {
      return getKey().compareTo(o.getKey());
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
}
