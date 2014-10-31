package org.gatein.rhq.plugins;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryCallback;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PortalDiscoveryCallback implements ResourceDiscoveryCallback
{

   private static final Log log = LogFactory.getLog("org.gatein.rhq.plugins");

   private static final String CONFIG_PRODUCT_NAME = "expectedRuntimeProductName";
   private static final String PRODUCT_NAME = "Portal";
   private static final String VERSION_RENAMED_TO_JBOSS_Portal = "6.1.1.GA";

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

      if (VersionComparator.instance().compare(VERSION_RENAMED_TO_JBOSS_Portal, version) <= 0) {
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
