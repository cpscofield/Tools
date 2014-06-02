// =============================================================================
// PriceFinder by Cary Scofield (carys689@gmail.com) is licensed under a 
// Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

import java.math.BigDecimal;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.PatternSyntaxException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.ConnectException;

/**
 * <p>
 * This is a small application to access a particular textbook web site
 * and programmatically retrieve the pricing information about a particular
 * book as specified by the ISBN property specified in the properties
 * file given as a command-line argument.
 * </p>
 * 
 * @since 1.7
 * @author Cary Scofield (carys689@gmail.com)
 * @date January 2013
 */
public final class PriceFinder {
    
    private static final String BOOK_SITE = "http://www.saveontextbooks.net/textbooks";
    private static final boolean DEBUGGING = false;
    private static final boolean PRINT_PAGE = false; // DEBUGGING needs to be set to true.
    private static final String NOT_AVAILABLE = "not available";
    private static HashSet<String> bookTypesToDisregard = null;
    
    static {
        bookTypesToDisregard = new HashSet<String>();
        bookTypesToDisregard.add( "Rent" );
        bookTypesToDisregard.add( "eBook" );
    }
    
    //static final String BOOK_SITE = "file:///C:/Users/SCOFIELD/Documents/Stash/Sandbox/JavaEx/NetBeansProjects/PriceFinder/saveontextbooks";
    /**
     * 
     * @param args[0]: The ISBN of the book we are trying to get prices for. 
     */
    public static void main(String[] args) {
        try {
            if( args == null || args.length == 0 || args[0] == null || args[0].length() == 0 ) {
                throw new IllegalArgumentException( "Missing properties file path from command line." );
            }
            
            final String ISBN = getProperties( args[0] ).getProperty( "ISBN" );

            PriceData prices = getPrices(ISBN, BOOK_SITE);

            System.out.println("");
            System.out.println("Price data is from " + prices.getURI().toString() );
            System.out.println("ALWAYS refer to this site for purchase details (ordering info, coupons, return policy, etc.)!!");
            System.out.println("==============================================================");
            System.out.println("");
            System.out.println("Title:      " + prices.getTitle());
            System.out.println("Edition:    " + prices.getEdition());
            System.out.println("List Price: " + prices.getListPrice());
            System.out.println("");
            
            if (prices.getPrices() != null && prices.getPrices().size() > 0) {
                System.out.format("%-20s %-22s %10s %10s %10s  %-10s%n",
                        "Seller", "Condition", "Price", "Shipping", "Total", "Currency");
                System.out.println("---------------------------------------------------------------------------------" );
                for (Price price : prices.getPrices() ) {
                    try {
                        if (price.getCondition().equals("New")) {
                            System.out.format("%-20s %-22s %10s %10s %10s  %-10s%n",
                                    price.getSeller(), price.getCondition(),
                                    format(price.getPrice()),
                                    format(price.getShippingCost()),
                                    format(price.getPrice().add(price.getShippingCost())),
                                    price.getCurrency());
                        } else {
                            System.out.format("%-20s %-22s %10s %10s %10s  %-10s%n",
                                    price.getSeller(), price.getCondition() + ": " +
                                    price.getUsedCondition(),
                                    format(price.getPrice()),
                                    format(price.getShippingCost()),
                                    format(price.getPrice().add(price.getShippingCost())),
                                    price.getCurrency());
                        }
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }
                summarizePriceData( prices.getPrices() );
            }
            else {
                System.out.println( "Sorry: no prices found for this book." );
            }

        } catch (IllegalArgumentException e ) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
    
    /**
     * An instantiation of this class retains all the pricing data we're
     * interested in.
     */
    private static class PriceData {
        private URI uri;
        private String title;
        private String edition;
        private String listPrice;
        private List<Price> prices;
        
        public URI getURI() { return this.uri; }
        public void setURI( final URI uri ) { this.uri = uri; }
        
        public String getTitle() { return this.title; }
        public void setTitle( final String title ) { this.title = title; }
        
        public String getEdition() { return this.edition; }
        public void setEdition( final String edition ) { this.edition = edition; }
        
        public String getListPrice() { return this.listPrice; }
        public void setListPrice( final String listPrice ) { this.listPrice = listPrice; }
        
        public List<Price> getPrices() { return this.prices; }
        public void setPrices( final List<Price> prices ) { this.prices = prices; }
        
    }
    
    /**
     * Group pricing data based on the condition of the book, then
     * produce statistics summarizing the price information according to condition.
     * 
     * @param prices 
     */
    private static void summarizePriceData( List<Price> prices ) {
        HashMap<String,List<Double>> conditionBuckets = new HashMap<String,List<Double>>();
        for( Price price : prices ) {
            BigDecimal totalPrice = price.getPrice().add(price.getShippingCost());
            if( price.getCondition().equals( "New"  ) ) {
                if( conditionBuckets.containsKey( "New" ) ) {
                    List<Double> bucketprices = conditionBuckets.get( "New" );
                    bucketprices.add( new Double( totalPrice.doubleValue() ) );
                }
                else {
                    List<Double> bucketprices = new ArrayList<Double>();
                    bucketprices.add( new Double( totalPrice.doubleValue() ) );
                    conditionBuckets.put( "New", bucketprices );
                }
            }
            else {
                StringBuilder usedKey = new StringBuilder();
                usedKey.append("Used");
                if (price.getUsedCondition().trim().length() > 0) {
                    usedKey.append(": " + price.getUsedCondition() );
                }
                if (conditionBuckets.containsKey(usedKey.toString())) {
                    List<Double> bucketprices = conditionBuckets.get(usedKey.toString());
                    bucketprices.add(new Double(totalPrice.doubleValue()));
               } else {
                    List<Double> bucketprices = new ArrayList<Double>();
                    bucketprices.add(new Double(totalPrice.doubleValue()));
                    conditionBuckets.put(usedKey.toString(), bucketprices);
               }
            }
        }
        
        System.out.println( "\nPrice summary:" );
        System.out.println(   "==============" );
        Set<String> keys = conditionBuckets.keySet();
        System.out.format( "%-22s %3s %10s %10s %10s%n", 
                "Condition", "Cnt", "Avg", "Min", "Max" );
        System.out.println( "----------------------------------------------------------" );
        for( String key: keys ) {
            List<Double> bucketprices = conditionBuckets.get( key );
            double total = 0.0D;
            int count = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for( Double price: bucketprices ) {
                count++;
                total += price.doubleValue();
                if( price < min ) min = price;
                if( price > max ) max = price;
            }
            System.out.format( "%-22s %3d %10s %10s %10s%n",
                    key, count, format(total/(double)count), format(min), format(max) );
        }
    }

    /**
     * Get title, edition, and pricing data from the page.
     * 
     * @param isbn
     * @return
     * @throws Exception 
     */
    private static PriceData getPrices(final String isbn, final String bookSite ) throws Exception {
        URI uri = new URI( bookSite + "/isbn/" + isbn );
        CharSequence page = readPage( uri );
        PriceData priceData = new PriceData();
        String title = getTitleFromPage( page );
        String edition = getEditionFromPage( page );
        String listPrice = getListPriceFromPage( page );
        List<Price> prices = getPricesFromPage( page );
        priceData.setURI( uri );
        priceData.setTitle( title );
        priceData.setEdition( edition );
        priceData.setListPrice( listPrice );
        priceData.setPrices( prices );
        return priceData;
    }
    
    private static String getTitleFromPage(CharSequence page) throws Exception {
        List<Match> titles = getTitles(page);
        if (titles != null) {
            if (titles.size() > 1) {
                System.err.println("Warning: more than one title found.");
            }
            else if( titles.size() == 0 ) {
                System.err.println("Warning: unable to elicit title data.");
                return NOT_AVAILABLE;
            }
            return titles.get(0).getText();
        } else {
            return NOT_AVAILABLE;
        }
    }
  
    private static String getEditionFromPage(CharSequence page) throws Exception {
        List<Match> editions = getEditions(page);
        if (editions != null) {
            if (editions.size() > 1) {
                System.err.println("Warning: more than one edition found.");
            }
            else if( editions.size() == 0 ) {
                System.err.println("Warning: unable to elicit edition data." );
                return NOT_AVAILABLE;
            }
            return editions.get(0).getText();
        } else {
            return NOT_AVAILABLE;
        }
    }
    
    private static String getListPriceFromPage(CharSequence page) throws Exception {
        List<Match> prices = getListPrice(page);
        if (prices != null) {
            if (prices.size() > 1) {
                System.err.println("Warning: more than one list price found.");
            } else if (prices.size() == 0) {
                System.err.println("Warning: unable to elicit list price data.");
                return NOT_AVAILABLE;
            }
            return format(Double.parseDouble(removeExtraneousPunc(prices.get(0).getText())));
        } else {
            return NOT_AVAILABLE;
        }
    }
 
    /**
     * Get all the pricing information from the page.
     * @param page
     * @return List of <tt>Price</tt> objects sorted in order of appearance on the page.
     * @throws Exception 
     */
    private static List<Price> getPricesFromPage(CharSequence page) throws Exception {
        long startTime = System.currentTimeMillis();
        List<Match> sellers = getSellers( page);
        List<Match> conditions = getConditions( page );
        List<Match> usedConditions = getUsedConditions( page );
        List<Match> prices = getPrices( page );
        List<Match> currencies = getCurrencies( page );
        List<Match> shippingCosts = getShippingCosts( page );
        long endTime = System.currentTimeMillis();
        System.out.println( ( endTime - startTime ) + " millisecs to elicit data from page." );
        startTime = System.currentTimeMillis();
        List<Match> all = sellers;
        all.addAll( conditions );
        all.addAll( usedConditions );
        all.addAll( prices );
        all.addAll( currencies );
        all.addAll( shippingCosts );
        List<Price> bookprices = sortAndCombine( all );
        endTime = System.currentTimeMillis();
        System.out.println( (endTime - startTime) + " millisecs to combine and sort the data." );
        return bookprices ;
    }
    
    /**
     * Sort all the matches by ascending offset and then combine into <tt>Price</tt> objects.
     * @param matches
     * @return 
     */
    private static List<Price> sortAndCombine(List<Match> matches) {
        ArrayList<Price> bookprices = new ArrayList<Price>();
        Match[] combined = new Match[matches.size()];
        matches.toArray(combined);
        Arrays.sort(combined);
        Price price = null;
        for (Match match : combined) {
            if( DEBUGGING ) System.out.println(match.toString());
            switch (match.getCategory()) {
                case SELLER:
                    if (price != null) {
                        if (DEBUGGING) {
                            System.out.println(price.toString());
                        }
                        if (!disregardBook(price.getCondition())) {
                            bookprices.add(price);
                        }
                    }
                    price = new PriceImpl();
                    price.setSeller(match.getText());
                    break;
                case CONDITION:
                    price.setCondition(match.getText());
                    break;
                case USED_CONDITION:
                    if ("VeryGood".equals(match.getText())) {
                        match.setText("Very Good"); // adjust for contraction
                    } else if ("LikeNew".equals(match.getText())) {
                        match.setText("Like New"); // adjust for contraction
                    }
                    price.setUsedCondition(match.getText());
                    break;
                case PRICE:
                    price.setPrice(new BigDecimal(removeExtraneousPunc(match.getText())));
                    break;
                case CURRENCY:
                    price.setCurrency(match.getText());
                    break;
                case SHIPPING_COST:
                    price.setShippingCost(new BigDecimal(match.getText()));
                    break;
                default:
                    /* Ignore? */
                    break;
            } // end switch
        } // end for
        if (price != null && !disregardBook(price.getCondition())) {
            if( DEBUGGING ) System.out.println(price.toString());
            bookprices.add(price); // the last one
        }
        return bookprices;
    }
    
    private static List<Match> getSellers(CharSequence page) throws Exception {
        return getMatches(Category.SELLER, page, "\\<span\\s+itemprop\\=\\\"seller\\\"\\s+style\\=\\\"font\\-weight\\:bold\\\"\\>([\\s\\S]+?)\\</span\\>");
    }

    private static List<Match> getConditions(CharSequence page) throws Exception {
        return getMatches(Category.CONDITION, page, "\\<b\\s+itemprop\\=\\\"itemCondition\\\"\\>([\\w ]+?)</b\\>");
    }

    private static List<Match> getUsedConditions(CharSequence page) throws Exception {
        return getMatches(Category.USED_CONDITION, page, "\\<span\\s+style\\=\\\"font\\-size:10px;\\\"\\s+itemprop\\=\\\"UsedCondition\\\"\\>([\\s\\S]+?)\\</span\\>");
    }

    private static List<Match> getPrices(CharSequence page) throws Exception {
        return getMatches(Category.PRICE, page, "\\<span\\s+itemprop\\=\\\"price\\\"\\>([\\d\\.\\,]+?)\\</span\\>");
    }

    private static List<Match> getCurrencies(CharSequence page) throws Exception {
        return getMatches(Category.CURRENCY, page, "\\<meta\\s+itemprop\\=\\\"priceCurrency\\\"\\s+content\\=\\\"(\\w+)\\\" />");
    }

    private static List<Match> getShippingCosts(CharSequence page) throws Exception {
        return getMatches(Category.SHIPPING_COST, page, "\\<td\\s+style\\=\\\"text\\-align:center;\\\"\\>\\+\\$([\\d\\.\\,]+?)\\</td\\>");
    }
    
    private static List<Match> getTitles(CharSequence page) throws Exception {
        return getMatches(Category.NOT_APPLICABLE, page, "\\<span\\s+itemprop\\=\\\"name\\\"\\>([\\s\\S]+?)\\</span\\>" );
    }
    
    private static List<Match> getEditions(CharSequence page) throws Exception {
        return getMatches(Category.NOT_APPLICABLE, page, "\\<span\\s+itemprop\\=\\\"bookEdition\\\"\\>([\\s\\S]+?)\\</span\\>");
    }
    
    private static List<Match> getListPrice(CharSequence page) throws Exception {
        return getMatches(Category.NOT_APPLICABLE, page, "List Price:\\s+([\\s\\S]+?)\\<br\\>");
    }

    /**
     * The regular expression engine for this application.
     * @param category
     * @param page
     * @param pattern
     * @return A list of <tt>Match</tt> objects found according to regex pattern.
     * @throws Exception 
     */
    private static List<Match> getMatches( final Category category, final CharSequence page, 
            final String pattern) throws Exception {
        ArrayList<Match> matches = new ArrayList<Match>();
        try {
            final Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(page);
            MatchResult result = matcher.toMatchResult();
            int numGroups = result.groupCount();
            //System.out.format("numGroups=%d%n", numGroups);
            int count = 0;
            int start = -1;
            int end = -1;
            while (matcher.find()) {
                ++count;
                start = matcher.start(1);
                end = matcher.end(1);
                String matchedText = page.subSequence(start, end).toString();
                //System.out.println(matchedText);
                Match match = new MatchImpl();
                match.setCategory( category );
                match.setOffset( start );
                match.setText( matchedText );
                //System.out.format( "from %d to %d: %s%n", start, end, matchedText );
                matches.add( match );
            }
            //System.out.println("count=" + count);

        } catch (PatternSyntaxException e) {
            System.err.println(e.toString());
            System.err.println("Description=" + e.getDescription());
            System.err.println("Index=" + e.getIndex());
            System.err.println("Pattern=" + e.getPattern());
            System.err.println("Message=" + e.getDescription());
            throw e;
        } catch (IllegalStateException e) {
            System.err.println(e.toString());
            throw e;
        }
        return matches;
    }

    /**
     * Read the page from the input stream.
     * @param is
     * @return A CharSequence object containing contents read from input stream.
     * @throws Exception 
     */
    private static StringBuilder readPage( URI uri ) throws Exception {
        InputStream is = openURL( uri );
        StringBuilder page = new StringBuilder();
        final int END_OF_FILE = -1;
        //final int MAX_CHAR_VALUE = 0xff;
        long startTime = System.currentTimeMillis();
        int ch;
        while( ( ch = is.read() ) != END_OF_FILE ) {
            page.append( (char)ch );
        }
        long endTime = System.currentTimeMillis();
        if( DEBUGGING && PRINT_PAGE) System.out.println( page.toString() );
        System.out.println( ( endTime - startTime ) + " millisecs to read page of " + page.length() + " bytes." ); 
        is.close();
        return page;
    }
    
    /**
     * Open the input stream and return it.
     * 
     * @param uri
     * @return the input stream.
     * @throws Exception 
     */
    private static InputStream openURL(final URI uri) throws Exception {
        final int CONNECTION_TIMEOUT = 180000;
        long startTime = 0; long endTime = 0;
        try {
            if (uri == null) {
                throw new IllegalArgumentException("uri is null");
            }
            final URL url = uri.toURL();
            startTime = System.currentTimeMillis();
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            final InputStream is = url.openStream();
            final int PAGE_SIZE = 4096;
            final int NUM_PAGES = 20;
            final BufferedInputStream bis = new BufferedInputStream(is, PAGE_SIZE * NUM_PAGES);
            return bis;
        } catch (ConnectException e) {
            endTime = System.currentTimeMillis();
            System.err.println( (endTime-startTime) + " millisecs before timeout." );
            throw e;
        } 
    }
   
    /**
     * Remove non-digital characters from price string, such as '$', ',', and/or ' '.
     * @param price
     * @return 
     */
    private static String removeExtraneousPunc( String price ) {
        StringBuilder buf = new StringBuilder();
        for( int i = 0, n = price.length(); i < n; ++i ) {
            char ch = price.charAt(i);
            if( Character.isDigit( ch ) ) buf.append( ch );
            else if( ch == '.' ) buf.append( ch );
            else { /* ignore */ }
        }
        return buf.toString();
    }
    
    private static Properties getProperties(String propertiesPath) throws Exception {
        File file = new File(propertiesPath);
        FileInputStream fis = new FileInputStream(file);
        Properties props = new Properties();
        props.load(fis);
        return props;
    }

    
    private static boolean disregardBook( String booktype ) {
        return bookTypesToDisregard.contains( booktype );
    }
    
    private static String format( BigDecimal value ) {
        return format( value.doubleValue() );
    }
    
    private static String format( double value ) {
        java.text.DecimalFormat df = new java.text.DecimalFormat( "#,##0.00" );
        return df.format( value, new StringBuffer(), new java.text.FieldPosition( 0 ) ).toString();
    }
}

/**
 * For categorizing a match.
 */
enum Category {
    SELLER
    , CONDITION
    , USED_CONDITION
    , PRICE
    , CURRENCY
    , SHIPPING_COST
    , NOT_APPLICABLE
    
}

/**
 * Interface to keep track of matches found by regex searches.
 */
interface Match {
    
    public void setOffset( final int offset);
    public int getOffset();
    
    public void setText( final String text );
    public String getText();
    
    public void setCategory( final Category category );
    public Category getCategory();
}

class MatchImpl implements Match, Comparable {
    
    private int offset;
    private String text;
    private Category category;
    
    @Override
    public int compareTo(Object obj ) {
        Match match = (Match)obj;
        if( this.offset < match.getOffset() ) return -1;
        else if( this.offset > match.getOffset() ) return 1;
        else return 0;
    }
    
    @Override
    public void setOffset( final int index ) { this.offset = index; }
    @Override
    public int getOffset() { return offset; }
    
    @Override
    public void setText( final String text ) { this.text = text; }
    @Override
    public String getText() { return text; }
    
    @Override
    public void setCategory( final Category category ) { this.category = category; }
    @Override
    public Category getCategory() { return this.category; }
    
    @Override
    public String toString() {
        final String SEP = "; ";
        StringBuilder buf = new StringBuilder();
        buf.append( "MATCH: " );
        buf.append( "offset: " ).append( this.offset );
        buf.append( SEP ).append( "category: " ).append( this.category );
        buf.append( SEP ).append( "text: " ).append( this.text );
        return buf.toString();
    }
    
}

/**
 * Interface to collect in one object pricing information for one seller.
 */
interface Price {
    public void setPrice( final BigDecimal price );
    public BigDecimal getPrice();
    
    public void setShippingCost( final BigDecimal shippingCost );
    public BigDecimal getShippingCost();
    
    public void setSeller( final String seller );
    public String getSeller();
    
    public void setCondition( final String condition );
    public String getCondition();
    
    public void setUsedCondition( final String usedCondition );
    public String getUsedCondition();
    
    public void setCurrency( final String currency );
    public String getCurrency();
}

class PriceImpl implements Price {

    private BigDecimal price = new BigDecimal( 0 );
    private BigDecimal shippingCost = new BigDecimal( 0 );
    private String seller;
    private String condition;
    private String usedCondition = "";
    private String currency = "USD";
    
    public PriceImpl() {}
    
    @Override
    public void setPrice( final BigDecimal price ) { this.price = price; }
    @Override
    public BigDecimal getPrice() { return this.price; }
    
    @Override
    public void setShippingCost( final BigDecimal shippingCost ) { this.shippingCost = shippingCost; }
    @Override
    public BigDecimal getShippingCost() { return this.shippingCost; }
    
    @Override
    public void setSeller( final String seller ) { this.seller = seller; }
    @Override
    public String getSeller() { return this.seller; }
    
    @Override
    public void setCondition( final String condition ) { this.condition = condition; }
    @Override
    public String getCondition() { return this.condition; }
    
    @Override
    public void setUsedCondition( final String usedCondition ) { this.usedCondition = usedCondition; }
    @Override
    public String getUsedCondition() { return this.usedCondition; }
    
    @Override
    public void setCurrency( final String currency ) { this.currency = currency; }
    @Override
    public String getCurrency() { return this.currency; }
    
    @Override
    public String toString() {
        final String SEP = "; ";
        StringBuilder buf = new StringBuilder();
        buf.append( "PRICE: " );
        buf.append( "Seller: " ).append( this.seller );
        buf.append( SEP ).append( "Condition: " ).append( this.condition );
        buf.append( SEP ).append( "Used Condition: " ).append( this.usedCondition );
        buf.append( SEP ).append( "Price: " ).append( this.price );
        buf.append( SEP ).append( "Currency: " ).append( this.currency );
        buf.append( SEP ).append( "Shipping Cost: " ).append( this.shippingCost );
        return buf.toString();
    }
}
