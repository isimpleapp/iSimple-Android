package com.treelev.isimple.utils.managers;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {

    private final static String GET_ROUTE_URL_FORMAT = "http://maps.googleapis.com/maps/api/directions/xml?origin=" +
            "%s,%s&destination=%s,%s&sensor=false&units=metric&mode=driving";
    private final static String START_NODE_TAG = "step";
    private final static String START_LOCATION_NODE_TAG = "start_location";
    private final static String LAT_NODE_TAG = "lat";
    private final static String LNG_NODE_TAG = "lng";
    private final static String POLYLINE_NODE_TAG = "polyline";
    private final static String POINTS_NODE_TAG = "points";
    private final static String END_LOCATION_NODE_TAG = "end_location";

    //ANALYTICS
//    public static List<LatLng> createRoute(LatLng src, LatLng dest) {
//        return createRouteList(getDocument(makeUrl(src, dest)));
//    }

    //ANALYTICS
//    private static String makeUrl(LatLng src, LatLng dest) {
//        return String.format(GET_ROUTE_URL_FORMAT, Double.toString(src.latitude), Double.toString(src.longitude),
//                Double.toString(dest.latitude), Double.toString(dest.longitude));
//    }

    private static Document getDocument(String urlString) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(urlString);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //ANALYTICS
//    private static List<LatLng> createRouteList(Document doc) {
//        NodeList nl1, nl2, nl3;
//        List<LatLng> listGeopoints = new ArrayList<LatLng>();
//        if (doc != null) {
//            nl1 = doc.getElementsByTagName(START_NODE_TAG);
//            if (nl1.getLength() > 0) {
//                for (int i = 0; i < nl1.getLength(); i++) {
//                    Node node1 = nl1.item(i);
//                    nl2 = node1.getChildNodes();
//                    Node locationNode = nl2.item(getNodeIndex(nl2, START_LOCATION_NODE_TAG));
//                    nl3 = locationNode.getChildNodes();
//                    Node latNode = nl3.item(getNodeIndex(nl3, LAT_NODE_TAG));
//                    double lat = Double.parseDouble(latNode.getTextContent());
//                    Node lngNode = nl3.item(getNodeIndex(nl3, LNG_NODE_TAG));
//                    double lng = Double.parseDouble(lngNode.getTextContent());
//                    listGeopoints.add(new LatLng(lat, lng));
//                    locationNode = nl2.item(getNodeIndex(nl2, POLYLINE_NODE_TAG));
//                    nl3 = locationNode.getChildNodes();
//                    latNode = nl3.item(getNodeIndex(nl3, POINTS_NODE_TAG));
//                    ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
//                    for (LatLng anArr : arr) {
//                        listGeopoints.add(new LatLng(anArr.latitude, anArr.longitude));
//                    }
//                    locationNode = nl2.item(getNodeIndex(nl2, END_LOCATION_NODE_TAG));
//                    nl3 = locationNode.getChildNodes();
//                    latNode = nl3.item(getNodeIndex(nl3, LAT_NODE_TAG));
//                    lat = Double.parseDouble(latNode.getTextContent());
//                    lngNode = nl3.item(getNodeIndex(nl3, LNG_NODE_TAG));
//                    lng = Double.parseDouble(lngNode.getTextContent());
//                    listGeopoints.add(new LatLng(lat, lng));
//                }
//            }
//        }
//        return listGeopoints;
//    }

    private static int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    //ANALYTICS
//    private static ArrayList<LatLng> decodePoly(String encoded) {
//        ArrayList<LatLng> poly = new ArrayList<LatLng>();
//        int index = 0, len = encoded.length();
//        int lat = 0, lng = 0;
//        while (index < len) {
//            int b, shift = 0, result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//            shift = 0;
//            result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
//            poly.add(position);
//        }
//        return poly;
//    }
}
