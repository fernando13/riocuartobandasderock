package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.Album;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.AlbumDAO;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.AlbumDaoImpl;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.SessionManager;
import spark.Request;
import spark.Response;

import spark.ModelAndView;

import spark.ModelAndView;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.Map;


public class AlbumController {
    protected static AlbumController unique_instance = null;

    public AlbumController() {
    }

    public static AlbumController getInstance() {
        if (unique_instance == null)
            unique_instance = new AlbumController();
        return unique_instance;
    }

    // public List<Album> getAll(Request req, Response res){
    public ModelAndView getAll(Request req, Response res){
        Map<String, Object> attributes = new HashMap<>();
    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);

    	List<Album> albums = adao.getAll();
    	session.close();
    	int status = albums.size() > 0 ? 200 : 204;
		res.status(status);
		res.body(albums.toString());
		// return albums;
        attributes.put("albumns", albums);
        attributes.put("template", Routes.index_album());
        return new ModelAndView(attributes, Routes.layout_dashboard());
    }

    public ModelAndView showAlbum(Request req,Response res){
        Map<String, Object> attributes = new HashMap<>();

        Session session = SessionManager.getInstance().openSession();
        AlbumDAO albumDAO = new AlbumDaoImpl(session);

        Album album = albumDAO.findById(req.params(":id"));
        attributes.put("album", album);

        attributes.put("template", Routes.show_album());
        attributes.put("title", "Show");
        return new ModelAndView(attributes, Routes.layout_dashboard());
    }

    public ModelAndView newAlbum(Request req,Response res){
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("template", Routes.new_album());
        attributes.put("title", "New");
        return new ModelAndView(attributes, Routes.layout_dashboard());
    }

    public ModelAndView editAlbum(Request req,Response res){
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("template", Routes.edit_album());
        attributes.put("title", "Edit");
        return new ModelAndView(attributes, Routes.layout_dashboard());
    }



    public ModelAndView create(Request req, Response res) {
        Map<String, Object> attributes = new HashMap<>();

    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);

        if (req.queryParams("title") == null || req.queryParams("title") == ""){
            res.status(400);
            // res.body("Album title can't be null nor empty");
            // return res.body();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            //Date should be in the next pattern: yyyy-mm-dd
        	Date release_date = req.queryParams("release_date") != null ? sdf.parse(req.queryParams("release_date")) : null;
        	Transaction transaction = session.beginTransaction();
            boolean result = adao.create(req.queryParams("title"), release_date);
            transaction.commit();
            session.close();
            int http_status = result ? 201 : 409;
            res.status(http_status);
            if (!result){
                res.body("Duplicate album"); //If the result of the creation was false, it means that there is a duplicate
                attributes.put("error", "El Album no puede tener el nombre en blanco");
                attributes.put("template", Routes.new_album());
                return new ModelAndView(attributes, Routes.layout_dashboard());
            }
            res.body("Album created");
            attributes.put("success", "El Album se creo con exito");
            attributes.put("template", Routes.index_album());
            return new ModelAndView(attributes, Routes.layout_dashboard());
            // return res.body();
        } catch (ParseException | IllegalArgumentException e) {
            //If an exception was thrown, then there was a problem with the parameters.
            e.printStackTrace();
            res.status(400);
            // res.body("Bad parameters. "+e.getMessage()+" \n" );
            // return res.body();
            attributes.put("error", "El Album no puede tener el nombre en blanco");
            attributes.put("template", Routes.new_album());
            return new ModelAndView(attributes, Routes.layout_dashboard());
        } catch (Exception e){
            e.printStackTrace();
            res.status(500);
            // res.body("Internal server error");
            // return res.body();
        }

    }

    public List<Album> findByTitle(Request req, Response res) {
    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);

    	if (req.queryParams("title") == null){
            res.status(400);
            res.body("Title can't be null");
            return null;
        }
        List<Album> albums = adao.findByTitle(req.queryParams("title"));
        session.close();
        int http_status = albums.size() > 0 ? 200 : 204;
        res.status(http_status);
        return albums;
    }

    public List<Album> findByReleaseDate(Request req, Response res){
    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);
    	if (req.queryParams("release_date") == null) {
            res.status(400);
            res.body("Release date can't be null");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date release_date = sdf.parse(req.queryParams("release_date"));


            List<Album> albums = adao.findByReleaseDate(release_date);
            session.close();
            int http_status = albums.size() > 0 ? 200 : 204;
            res.status(http_status);
            return albums;
        } catch (ParseException e){
            e.printStackTrace();
            res.status(400);
            res.body("Bad date");
            return null;
        }


    }

    public String update(Request req, Response res) {
    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);

        if (req.queryParams("title") == null || req.queryParams("title") == ""){
            res.status(400);
            res.body("Album title can't be null nor empty");
            return res.body();
        }
        if (req.queryParams("id") == null || req.queryParams("id") == ""){
            res.status(400);
            res.body("Album id can't be null nor empty");
            return res.body();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        try {
            //Date should be in the next pattern: yyyy-mm-dd
        	Date release_date = req.queryParams("release_date") != null ? sdf.parse(req.queryParams("release_date")) : null;
        	Transaction transaction = session.beginTransaction();
            boolean result = adao.update(req.queryParams("id"), req.queryParams("title"), release_date);
            transaction.commit();
            session.close();
            int http_status = result ? 201 : 409;
            res.status(http_status);
            if (!result) res.body("Duplicate album"); //If the result of the creation was false, it means that there is a duplicate
            res.body("Album updated");
            return res.body();
        } catch (ParseException | IllegalArgumentException e) {
            //If an exception was thrown, then there was a problem with the parameters.
            e.printStackTrace();
            res.status(400);
            res.body("Bad parameters. "+e.getMessage()+" \n" );
            return res.body();
        } catch (Exception e){
            e.printStackTrace();
            res.status(500);
            res.body("Internal server error");
            return res.body();
        }

    }

    public String delete(Request req, Response res) {
    	Session session = SessionManager.getInstance().openSession();
    	AlbumDaoImpl adao = new AlbumDaoImpl(session);
        if (req.queryParams("id") == null || req.queryParams("id") == ""){
            res.status(400);
            res.body("Album id can't be null nor empty");
            return res.body();
        }

        Transaction transaction = session.beginTransaction();
        boolean result = adao.delete(req.queryParams("id"));
        transaction.commit();
        session.close();
        int http_status = result ? 201 : 409;
        res.status(http_status);
        if (!result) res.body("Album doesn't exist");
        res.body("Album deleted");
        return res.body();
    }


}
