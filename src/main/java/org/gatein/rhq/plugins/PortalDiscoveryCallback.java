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

   private static final Map<String, String> productNameMap;

   static
   {
      Map<String, String> map = new HashMap<String, String>();
      map.put("6.1.1", "Portal");

      productNameMap = map;
   }

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
      for (String v : productNameMap.keySet())
      {
         if (version.contains(v))
         {
            String productName = productNameMap.get(v);
            if (trace)
            {
               String before = discoveredResourceDetails.getPluginConfiguration().getSimpleValue(CONFIG_PRODUCT_NAME);
               log.trace("Modifying " + CONFIG_PRODUCT_NAME + " for version " + version + ", from " + before + " to " + productName);
            }
            discoveredResourceDetails.getPluginConfiguration().setSimpleValue(CONFIG_PRODUCT_NAME, productName);
            result = DiscoveryCallbackResults.PROCESSED;
         }
      }

      return result;
   }

   // This isn't quite the best way to know it's a portal server, as we are relying on the JPP string
   private boolean isPortalServer(DiscoveredResourceDetails details)
   {
      return details.getResourceType().getName().contains("JPP") && details.getResourceVersion().contains("JPP");
   }
}
