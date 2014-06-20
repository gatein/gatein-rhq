package org.gatein.rhq.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PortalDiscoveryCallback implements ResourceDiscoveryCallback
{

   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins");

   private static final String CONFIG_PRODUCT_NAME = "expectedRuntimeProductName";
   private static final String PRODUCT_NAME = "Portal";

   @Override
   public DiscoveryCallbackResults discoveredResources(DiscoveredResourceDetails discoveredResourceDetails) throws Exception
   {
      DiscoveryCallbackResults result = DiscoveryCallbackResults.UNPROCESSED;
      if (!isPortalServer(discoveredResourceDetails))
      {
         return result;
      }

      boolean trace = log.isTraceEnabled();
      String version = discoveredResourceDetails.getResourceVersion();
      String name = discoveredResourceDetails.getResourceName();

      // small hack: we check if the version is higher than 6.1.1, as the logic is the same for all known subsequent versions
      // first, we discard everything after the last dot, as it's usually the release-level string (GA, ER1, ER2, CR1...)
      // then, we get only the numeric chars, and the resulting should be a string with length of 3
      String cleanedVersion = version.substring(0, version.lastIndexOf(".")).replaceAll("[^\\d]", "");
      if (null == cleanedVersion || cleanedVersion.length() > 3)
      {
         throw new IllegalArgumentException("Couldn't reliably determine the Portal version for '" + version + "'");
      }

      int versionAsInteger = Integer.parseInt(cleanedVersion);
      if (versionAsInteger >= 611)
      {
         if (trace)
         {
            String before = discoveredResourceDetails.getPluginConfiguration().getSimpleValue(CONFIG_PRODUCT_NAME);
            log.trace("Modifying " + CONFIG_PRODUCT_NAME + " for resource [Name=" + name + ", Version=" + version + "] from '" + before + "' to '" + PRODUCT_NAME + "'");
         }
         discoveredResourceDetails.getPluginConfiguration().setSimpleValue(CONFIG_PRODUCT_NAME, PRODUCT_NAME);
         result = DiscoveryCallbackResults.PROCESSED;
      }

      return result;
   }

   // This isn't quite the best way to know it's a portal server, as we are relying on the JPP string
   private boolean isPortalServer(DiscoveredResourceDetails details)
   {
      return details.getResourceName().startsWith("JPP");
   }
}
