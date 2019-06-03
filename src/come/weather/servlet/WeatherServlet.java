package com.weather.servlet;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.json.JSON;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.weather.util.WeatherConstants;
import com.weather.util.WeatherLiteral;
 
@WebServlet("/weatherServlet")

public class WeatherServlet extends HttpServlet {
	/**
     * this life-cycle method is invoked when this servlet is first accessed
     * by the client
     */
    public void init(ServletConfig config) {
    }
 
	
	  /**
     * handles HTTP GET request
	 * @throws ServletException 
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
 
        PrintWriter writer = response.getWriter();
        writer.println("<html>Hello,Welcome to Weather report</html>");
        writer.flush();
        
    

      
    }
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    		
    	  String city=null;
          // read form fields
           city = request.getParameter(WeatherLiteral.CITY);
          String forcast =request.getParameter(WeatherLiteral.FORECAST);
          String current =request.getParameter(WeatherLiteral.CURRENT);

           
         // System.out.println("City: " + city);
          
          if (city==null ||" ".equals(city)) {
        	 
        	  throw new ServletException("Mandatory Parameter missing");
          }
   
          // do some processing here...
           
          // get response writer
          PrintWriter writer = response.getWriter();
           
                    
          HttpURLConnection con = null ;
          InputStream is = null;
         
          try {
              StringBuffer buffer = new StringBuffer();
             if (forcast!= null) {
                 con = (HttpURLConnection) ( new URL(WeatherConstants.BASE_URL_FORECAST +  "&key=" + WeatherConstants.APPID+ "&q="+city)).openConnection();
     
             }
             if(current!=null) {
                 con = (HttpURLConnection) ( new URL(WeatherConstants.BASE_URL_CURRENT +  "&key=" + WeatherConstants.APPID+ "&q="+city)).openConnection();

             }
             con.setRequestProperty(WeatherLiteral.CONTENT_TYPE, WeatherConstants.CONTENT_TYPE_UTF);
             con.setRequestProperty(WeatherLiteral.ACCEPT, WeatherConstants.ACCEPT_JSON);
           con.setRequestMethod(WeatherConstants.GET_METHOD);
           con.setDoInput(true);
           con.setDoOutput(true);
           con.connect();
          
           

         
           // Let's read the response
           is = con.getInputStream();
           
           BufferedReader br = new BufferedReader(new InputStreamReader(is));
           String line = null;
           while ( (line = br.readLine()) != null )
             buffer.append(line + "rn");
         
           is.close();
           con.disconnect();
       
           JSONObject json = new JSONObject(buffer.toString());
           String html = "<html>";

         
           if(forcast!=null) {
          	 //build Forecast HTML
          	html= createForecastHTML(city,json,html);
           }
           if (current!=null) {
          	html= createCurrentHTML(city,json,html);
           }
        // return response
           writer.println(html);
           
           
        
            
          	
         }
         catch(Throwable t) {

     	      processError(request, response, con);
          t.printStackTrace();
         }
         finally {
          try { is.close(); } catch(Throwable t) {}
          try { con.disconnect(); } catch(Throwable t) {}
         }
         
         

    	        
    }


	/**
	 * @param request
	 * @param response
	 * @param con
	 * @throws IOException
	 * @throws ServletException
	 */
	private void processError(HttpServletRequest request, HttpServletResponse response, HttpURLConnection con)
			throws IOException, ServletException {
		
		 JSONObject json = errorResponseObj(con);
		  int iCode= json.getJSONObject(WeatherLiteral.ERROR).getInt(WeatherLiteral.CODE);
          String strMessage= json.getJSONObject(WeatherLiteral.ERROR).getString(WeatherLiteral.MESSAGE);
		  
	
			 	         
	           
			 request.setAttribute(WeatherLiteral.ERROR_CODE, iCode);
			 if(iCode==1003) {
				 request.setAttribute(WeatherLiteral.ERROR_MESSAGE,"Please enter City");

			 }
			 else {
				 request.setAttribute(WeatherLiteral.ERROR_MESSAGE,strMessage);

			 }

		  
		    request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
 	     
	}


	/**
	 * @param con
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject errorResponseObj(HttpURLConnection con) throws IOException, JSONException {
		// Let's read the response
        InputStream is = con.getErrorStream();
        StringBuffer buffer = new StringBuffer();

         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String line = null;
         while ( (line = br.readLine()) != null )
           buffer.append(line + "rn");
       
         JSONObject json = new JSONObject(buffer.toString());

         is.close();
         con.disconnect();
		return json;
	}


	
    private String createCurrentHTML(String city, JSONObject json, String html) {
		
    	JSONObject currentJson=json.getJSONObject(WeatherLiteral.CURRENT);
        double strTemp_c=currentJson.getDouble(WeatherLiteral.TEMP_C);

        String strIcon=currentJson.getJSONObject(WeatherLiteral.CONDITION).getString(WeatherLiteral.ICON);
        String strText=currentJson.getJSONObject(WeatherLiteral.CONDITION).getString(WeatherLiteral.TEXT);
        System.out.println(strText);
		 String current="Currently";


        
       // html+="<h1 style="+'"' +"color:blue ;" +'"';
        html = createHTML(city, html, strTemp_c, strIcon,strText,current);

    	return html; 
	}


	/**
	 * @param city
	 * @param html
	 * @param strTemp_c
	 * @param strIcon
	 * @return
	 */
    
	private String createHTML(String city, String html, double strTemp_c, String strIcon,String strText,String report) {
		html+="<style type=\"text/css\">\r\n" + 
				"	.form {\r\n" + 
				"	 border: 2px solid black;\r\n" + 
				"  outline: #lightblue solid 10px;\r\n" + 
				"  margin: auto;  \r\n" + 
				"  padding: 20px;\r\n" + 
				"  text-align: center;}";
		html+="</style>";
		html+="<body style=\"background-color:powderblue;\">";
		html+="<div class=\"form\">";
		html+="<h1 style=\"background-color:LightGray;\"> ";
		if(report.equals("Forecast")) {
			html+="Forecast Weather at ";	
		}
		else {
			html+=report +" Weather at " ;
		}
		
        html +=  city + "<br/> <br/>"; 
        html+=strText;
        html+="</h1>";
        html+= "<img src="+'"' + strIcon+'"' ;
        html += "</img>"+strTemp_c;
        html+=" <sup> .C </sup>";
        //html += "<button onclick=\"  \" value=\" Test \"/>";
        html+="<body style=\"background-color:powderblue;\">";
        html+="<br/>";
        html+="Please go back to ";
        html+="<a href=\"/myapp/mainPage.jsp\" >" + "enter another City </a>";
      
        html+="</div>";
        html+="</body>";
        html += "</html>";
        System.out.println("strTemp_c:"+strTemp_c);
        System.out.println("ICON"+strIcon);
        System.out.println("html"+html);
		return html;
	}


	private String createForecastHTML(String city, JSONObject json, String html) {
		// TODO Auto-generated method stub
		JSONArray arr = json.getJSONObject(WeatherLiteral.FORECAST).getJSONArray(WeatherLiteral.FORECAST_DAY);
		 double dMaxtemp_c=0.0;
		 String strIcon="";
		 String strText="";
		 String forecast="Forecast";
        for (int i = 0; i < arr.length(); i++) {
        	  dMaxtemp_c= arr.getJSONObject(i).getJSONObject(WeatherLiteral.DAY).getDouble(WeatherLiteral.MAXTEMP_C);
    	      strIcon= arr.getJSONObject(i).getJSONObject(WeatherLiteral.DAY).getJSONObject(WeatherLiteral.CONDITION).getString(WeatherLiteral.ICON);
    	      strText=arr.getJSONObject(i).getJSONObject(WeatherLiteral.DAY).getJSONObject(WeatherLiteral.CONDITION).getString(WeatherLiteral.TEXT);


            System.out.println(dMaxtemp_c);
        }
        
        html = createHTML(city, html, dMaxtemp_c, strIcon,strText,forecast);


	    	return html; 
		
	}


	/**
     * this life-cycle method is invoked when the application or the server
     * is shutting down
     */
    public void destroy() {
        System.out.println("Servlet is being destroyed");
    }
 
}