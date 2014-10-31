package org.gatein.rhq.plugins;

import java.util.Comparator;

/**
 * Comparator for software versions like {@code "6.2"}, {@code "6.2.0"}, {@code "6.2.0.GA"}, etc.
 *
 * @author <a href="mailto:ppalaga@redhat.com">Peter Palaga</a>
 */
public class VersionComparator implements Comparator<String>
{
   /**
    * A stream of version string parts.
    */
   static class VersionTokenizer
   {
      private int position = 0;
      private final int length;
      private final String source;
      private int counter = 0;
      private final int numericComponentsCount;

      public VersionTokenizer(String source, int numericComponentsCount)
      {
         super();
         this.source = source;
         this.numericComponentsCount = numericComponentsCount;
         this.length = source.length();
      }

      /**
       * @return next version part delimited either by period or end of input
       */
      private String next()
      {
         if (position >= length)
         {
            return null;
         }
         int end = source.indexOf(DELIMITER, position);
         int start = position;
         if (end >= 0)
         {
            position = end + 1;
         } else
         {
            position = length;
            end = length;
         }
         return source.substring(start, end);
      }

      /**
       * @return next {@code int} version part delimited either by period or end of input
       * @throws NumberFormatException if the current part cannot be parset as {@code int}
       * @throws IllegalArgumentException if some of the parts is parsed into a negative integer
       */
      public int nextInt()
      {
         if (counter >= numericComponentsCount)
         {
            throw new IllegalStateException(this.getClass().getSimpleName()
                  + ".nextInt() cannot be invoked more than "
                  + numericComponentsCount + " times.");
         }
         counter++;
         String token = next();
         if (token == null)
         {
            return DEFAULT_NUMERIC_COMPONENT;
         }
         int result = Integer.valueOf(token);
         if (result < 0)
         {
            throw new IllegalArgumentException("Version string '" + source
                  + "' contains negative component " + result);
         }
         return result;
      }

      /**
       * @return the rest of the input from the current position to the input end
       */
      public String rest()
      {
         if (position >= length)
         {
            return DEFAULT_QUALIFIER;
         }
         int start = position;
         position = length;
         return source.substring(start);
      }

   }

   /**
    * @return the singleton instance of {@link VersionComparator}
    */
   public static VersionComparator instance()
   {
      return INSTANCE;
   }

   /** The singleton. */
   private static final VersionComparator INSTANCE = new VersionComparator();

   /** The period character - the delimiter of version parts. */
   public static final int DELIMITER = '.';

   /** Literal {@code "GA"} returned if there is no qualifier in the input. */
   public static final String DEFAULT_QUALIFIER = "GA";

   /** Literal {@code 0} returned if the input is missing minor or micro version part. */
   public static final int DEFAULT_NUMERIC_COMPONENT = 0;

   /** {@code 3} - the number of numeric parts in a version string. */
   public static final int MAJOR_MINOR_MICRO = 3;

   /** For thrown exceptions, see {@link VersionTokenizer#nextInt()}.
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(String version1, String version2)
   {
      VersionTokenizer t1 = new VersionTokenizer(version1, MAJOR_MINOR_MICRO);
      VersionTokenizer t2 = new VersionTokenizer(version2, MAJOR_MINOR_MICRO);
      for (int i = 0; i < MAJOR_MINOR_MICRO; i++)
      {
         int compare = Integer.compare(t1.nextInt(), t2.nextInt());
         if (compare != 0)
         {
            return compare;
         }
      }
      return t1.rest().compareTo(t2.rest());
   }

}
