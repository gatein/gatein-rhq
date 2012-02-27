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

import org.rhq.core.domain.configuration.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class TemplateResolver
{
   private static final Pattern TEMPLATE_PATTERN = Pattern.compile("%([^%]+)%");

   private TemplateResolver()
   {
   }

   public static String resolve(String objectName, Configuration configuration)
   {
      Matcher m = TEMPLATE_PATTERN.matcher(objectName);

      while (m.find())
      {
         String propName = m.group(1);

         String replacementValue = configuration.getSimpleValue(propName, null);
         objectName = objectName.replaceAll("%" + propName + "%", replacementValue);

         m = TEMPLATE_PATTERN.matcher(objectName);
      }

      return objectName;
   }
}
