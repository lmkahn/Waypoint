package com.lauren.waypoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lauren on 4/29/15.
 */
public class Yelp {
    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "dinner";
    private static final String DEFAULT_SW_LATITUDE = "37.9";
    private static final String DEFAULT_SW_LONGITUDE = "-122.5";
    private static final String DEFAULT_NE_LATITUDE = "37.78";
    private static final String DEFAULT_NE_LONGITUDE = "-122.3";
    private static final int SEARCH_LIMIT = 3;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";


    private static final String CONSUMER_KEY = "7DUGa3H7ieIVAPhci6DSEA";
    private static final String CONSUMER_SECRET = "Zt5rW_up3mcPL23wWZbnSfSjiU4";
    private static final String TOKEN = "Y12UnPT3U24-L83_9aQyoGfqurnzZ4dV";
    private static final String TOKEN_SECRET = "wae9nszIWHeaF0_N4JPvYHfqKJQ";

    OAuthService service;
    Token accessToken;

    public String term = DEFAULT_TERM;
    public String swLat = DEFAULT_SW_LATITUDE;
    public String swLong = DEFAULT_SW_LONGITUDE;
    public String neLat = DEFAULT_NE_LATITUDE;
    public String neLong = DEFAULT_NE_LONGITUDE;

    /**
     * Setup the Yelp API OAuth credentials and take in parameters
     *
     * @param yelpTerm term to query Yelp for
     * @param yelpSWLat southwest latitude of bounding box for yelp location to query for
     * @param yelpSWLong southwest longitude of bounding box for yelp location to query for
     * @param yelpNELat northeast latitude of bounding box for yelp location to query for
     * @param yelpNELong northeast longitude of bounding box for yelp location to query for
     */
    public Yelp(String yelpTerm, String yelpSWLat, String yelpSWLong, String yelpNELat, String yelpNELong){
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        this.accessToken = new Token(TOKEN, TOKEN_SECRET);
        this.term = yelpTerm;
        this.swLat = yelpSWLat;
        this.swLong = yelpSWLong;
        this.neLat = yelpNELat;
        this.neLong = yelpNELong;
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term <tt>String</tt> of the search term to be queried
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String term, String swLat, String swLong, String neLat, String neLong) {
        String boundsString = swLat + "," + swLong + "|" + neLat + "," + neLong;
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("bounds", boundsString);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link org.scribe.model.Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        System.out.println("Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    /**
     * Queries the Search API based on the command line arguments and takes the first result to query
     * the Business API.
     */
    public ArrayList<HashMap<String, String>> queryAPI() {
        JSONObject response = queryAPIJSONResponse();
        ArrayList<HashMap<String, String>> listOfYelpResults = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < SEARCH_LIMIT; i++){
            HashMap<String, String> result = queryAPIForBusiness(response, i);
            listOfYelpResults.add(result);
        }
        return listOfYelpResults;
    }

    public JSONObject queryAPIJSONResponse(){
        String searchResponseJSON = searchForBusinessesByLocation(this.term, this.swLat, this.swLong, this.neLat, this.neLong);

        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(searchResponseJSON);
        } catch (ParseException pe) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }
        return response;
    }

    public HashMap<String, String> queryAPIForBusiness(JSONObject response, int numBusiness){
        JSONArray businesses = (JSONArray) response.get("businesses");
        JSONObject theBusiness = (JSONObject) businesses.get(numBusiness);
        String businessID = theBusiness.get("id").toString();

        String businessResponseJSON = searchByBusinessId(businessID);
        System.out.println(String.format("Result for business \"%s\" found:", businessID));
        System.out.println(businessResponseJSON);

        String businessName = getBusinessName(theBusiness);
        System.out.println("Name: " + businessName);
        Float businessRating = getBusinessRating(theBusiness);
        System.out.println("Rating: " + businessRating);
        String businessAddress = getBusinessAddress(theBusiness);
        System.out.println("Address: " + businessAddress);
        String businessYelpLink = getBusinessYelpLink(theBusiness);
        System.out.println("Yelp Link: " + businessYelpLink);
        String[] businessCoords = getBusinessLatLongCoords(theBusiness);
        System.out.println("Coords: " + businessCoords[0] + " " + businessCoords[1]);
        String businessCategories = getBusinessCategories(theBusiness);
        System.out.println("Categories: " + businessCategories);

        HashMap<String, String> yelpHashMap = new HashMap<String, String>();
        yelpHashMap.put("id", businessID);
        yelpHashMap.put("name", businessName);
        yelpHashMap.put("rating", businessRating.toString());
        yelpHashMap.put("address", businessAddress);
        yelpHashMap.put("link", businessYelpLink);
        yelpHashMap.put("latitude", businessCoords[0]);
        yelpHashMap.put("longitude", businessCoords[1]);
        yelpHashMap.put("categories", businessCategories);
        return yelpHashMap;

    }

    /**
     * Creates and sends a request to the Business API by business ID.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    public String getBusinessName(JSONObject business){
        return (String) business.get("name");
    }

    public Float getBusinessRating(JSONObject business){
        double rating = (double) business.get("rating");
        float floatRating = (float) rating;
        return (Float) floatRating;
    }

    public String getBusinessAddress(JSONObject business){
        JSONObject location = (JSONObject) business.get("location");
        String street = location.get("address").toString();
        String streetSubstring = street.substring(2, street.length()-2);
        String city = (String) location.get("city");
        String state = (String) location.get("state_code");
        return streetSubstring + " " + city + ", " + state;
    }

    public String getBusinessYelpLink(JSONObject business){
        return (String) business.get("mobile_url");
    }

    public String[] getBusinessLatLongCoords(JSONObject business){
        JSONObject location = (JSONObject) business.get("location");
        JSONObject coordinates = (JSONObject) location.get("coordinate");
        Double latitude = (Double) coordinates.get("latitude");
        Double longitude = (Double) coordinates.get("longitude");
        return new String[]{latitude.toString(), longitude.toString()};
    }

    public String getBusinessCategories(JSONObject business){
        JSONArray categoriesArray = (JSONArray) business.get("categories");
        String categories = "";
        for(Object category : categoriesArray){
            int comma = category.toString().indexOf(",");
            String categorySubstring = category.toString().substring(2, comma-1);
            categories+= categorySubstring + ",";
        }
        String finalCategories = categories.substring(0,categories.length()-1);
        return finalCategories;
    }


    public static void main(String[] args) {
        Yelp yelp = new Yelp("lunch","37.9", "-122.5", "37.78", "-122.3");
        yelp.queryAPI();
    }


}
