package country.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import country.dao.Country;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/countries", "/addCountry"})
public class CountryServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException , IOException{
		
        displayCountries(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException , IOException{
		
		String countryName = request.getParameter("countryName");
		
		if (countryName != null && !countryName.trim().isEmpty()) {
			boolean success = Country.add(countryName);
			
			if(success) {
				System.out.println("Pays Ajouter avec succes : " +countryName);
			}else {
				System.out.println("Erreur lors ajoute du pays !!!");
			}
		}
        displayCountries(request, response);
	}
	
	private void displayCountries (HttpServletRequest request, HttpServletResponse response) 
	throws IOException {
		
		response.setContentType("text/html; charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		
		try {
			List<Country> countries = Country.get();
			
	        out.println("<!DOCTYPE html>");
            out.println("<html lang='fr'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <title>Liste des Pays</title>");
            // styles CSS 
            out.println("    <style>");
            out.println("        body { font-family: Arial, sans-serif; margin: 40px; }");
            out.println("        h1 { color: #333; }");
            out.println("        ul { list-style-type: disc; margin-left: 20px; }");
            out.println("        li { margin: 5px 0; }");
            out.println("        a { text-decoration: none; color: #007bff; }");
            out.println("        a:hover { text-decoration: underline; }");
            out.println("    </style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("    <h1>Liste des pays(" +countries.size() + " pays)</h1>");
            
            String userEmail = (String) request.getSession(false) != null ? 
                    (String) request.getSession(false).getAttribute("userEmail") : null;
            
            if(userEmail != null) {
            	out.println(" <p>Connecté en tant que : <strong>\" + userEmail + \"</strong> |");
                out.println("    <a href='logout'>Se déconnecter</a></p>");
            }	
            
            out.println("    <p><a href='addCountry.html'>Ajouter un nouveau pays</a></p>");
            out.println("    <ul>");
            
            for(Country country : countries){
            	out.println("   <li>" + country.getCountry() + "</li>");
            }
            out.println("    </ul>");
            out.println("</body>");
            out.println("</html>");

		}finally {
			out.close();
		}
	}
}
