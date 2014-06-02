// =============================================================================
// CryptoLister by Cary Scofield (carys689@gmail.com) is licensed under 
// a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.security.Provider;
import java.security.Security;

/**
 * This program is a utility that simply lists all the cryptographic algorithms 
 * available in your JDK installation grouped by service type.
 * 
 * @author Cary Scofield (carys689@gmail.com)
 * @since 1.7
 */
public class CryptoLister {
    
    public static void main( String[] args ) {
        listAlgorithmsByType();
    }
    
    /**
     * List cryptographic algorithms by service type.
     * <p>
     * The main data structure is a hash map using service types as the keys.
     * Each value entry in the hash map is a list containing the names of the
     * algorithms for that service type.
     * </p>
     */
    public static void listAlgorithmsByType() {
        HashMap<String,ArrayList> algorithmTypes = 
                new HashMap<String,ArrayList>();
        Provider[] providers = Security.getProviders();
        
        //
        // Build the hash map.
        //
        for( Provider provider : providers ) {
            Set<Provider.Service> services = provider.getServices();
            Iterator<Provider.Service> servicesIter = services.iterator();
            while( servicesIter.hasNext() ) {
                Provider.Service service = servicesIter.next();
                ArrayList<Provider.Service> serviceList = 
                        algorithmTypes.get( service.getType() );
                if( serviceList == null ) {
                    serviceList = new ArrayList<Provider.Service>();
                    serviceList.add( service );
                    algorithmTypes.put( service.getType(), serviceList);
                }
                else {
                    serviceList.add( service );
                }
            }
        }
        
        //
        // Dump the contents of the hash map.
        //
        Set<String> types = algorithmTypes.keySet();
        Iterator<String> typeIter = types.iterator();
        while( typeIter.hasNext() ) {
            String type = typeIter.next();
            System.out.println(type);
            ArrayList<Provider.Service> serviceList =
                    algorithmTypes.get(type);
            Iterator<Provider.Service> services = serviceList.iterator();
            while( services.hasNext() ) {
                Provider.Service service = services.next();
                System.out.format( "\t%s%n", service.getAlgorithm() );
            }
        }
    }
}
