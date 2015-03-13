package Assign;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C0644696
 */
@Path("/products")
public class products {

    /**
     * Provides GET /servlet and GET /servlet?id=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
    @GET
    @Produces("application/json")
    public String doGet() {

        return getResults("SELECT * FROM PRODUCT");
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String doGet(@PathParam("id") String id) {

        String res = getResults("SELECT * FROM PRODUCT where product_ID = ?", id);
        return res;
    }

    /**
     * Provides POST /servlet?name=XXX&age=XXX
     *
     * @param request - the request object
     * @param response - the response object
     */
    @POST
    @Consumes("application/json")
    public Response doPost(JsonObject object) {

        String names = object.getString("Name");
        String description = object.getString("Description");
        String quantity = object.getString("Quantity");

        doUpdate("INSERT INTO PRODUCT ( Name, Description, Quantity) VALUES ( ?, ?, ?)", names, description, quantity);

        int id = getId("SELECT product_ID from PRODUCT WHERE Name = ? AND Description = ?  ", names, description);
        return Response.ok("http://localhost:8080/CPD-4414_Assignment5-master/webresources/products" + id).build();
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response doPut(@PathParam("id") String id, JsonObject object) {
        
        String names = object.getString("Name");
        String description = object.getString("Description");
        String quantity = object.getString("Quantity");

        doUpdate("UPDATE PRODUCT SET product_ID = ?, Name = ?, Description = ?, Quantity = ? WHERE PRODUCT_ID = ?", id, names, description, quantity, id);

        return Response.ok("http://localhost:8080/CPD-4414_Assignment5-master/webresources/products" + id).build();
    }

    @DELETE
    @Path("{id}")
    public void doDelete(@PathParam("id") String id, String str) {

        doUpdate("DELETE FROM PRODUCT WHERE product_ID = ?", id);

    }

    private String getResults(String query, String... params) {

        StringWriter out = new StringWriter();
        JsonGeneratorFactory f = Json.createGeneratorFactory(null);
        JsonGenerator generator = f.createGenerator(out);

        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();

            generator.writeStartArray();

            while (rs.next()) {

                generator.writeStartObject()
                        .write("productId", rs.getInt("product_ID"))
                        .write("name", rs.getString("Name"))
                        .write("description", rs.getString("Description"))
                        .write("quantity", rs.getInt("Quantity"))
                        .writeEnd();

            }

            generator.writeEnd();
            generator.close();
            /*     sb.append(String.format("%s\t%s\t%s\t%s\n", rs.getInt("product_id"), rs.getString("product_name"), rs.getString("product_description"), rs.getInt("quantity")));

             }*/
        } catch (SQLException ex) {
            Logger.getLogger(products.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return out.toString();
    }

    private int doUpdate(String query, String... params) {
        int updtd_num = 0;
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            updtd_num = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return updtd_num;
    }

    private int getId(String query, String name, String desc) {
        int id = 0;
        try (Connection conn = credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "name");
            pstmt.setString(2, "desc");

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("product_ID");
            }

        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }
}
